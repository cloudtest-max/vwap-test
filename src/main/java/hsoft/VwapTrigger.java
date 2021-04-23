package hsoft;

import com.hsoft.codingtest.DataProvider;
import com.hsoft.codingtest.DataProviderFactory;
import com.hsoft.codingtest.MarketDataListener;
import com.hsoft.codingtest.PricingDataListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;


public class VwapTrigger {
  static Logger logger = Logger.getLogger(VwapTrigger.class);
  static Logger testProductLogger  = Logger.getLogger("TEST_PRODUCT");

  private Map<String, Double> fairValueMap = null;
  private Map<String, MarketValueQueue> marketValueMap = null;
  
  public void writeResults(Logger logger, String productId, Double fairValue, Double averageMarketValue, int result) {

    if (result > 0) {
      logger.info("ProductID:"+ productId + ", VWAP("+ averageMarketValue + ") > FairValue(" + fairValue + ")");
    } else if (result == 0) {
      logger.info("ProductID:"+ productId + ", VWAP("+ averageMarketValue + ") = FairValue(" + fairValue + ")");
    } else if (result < 0) {
      logger.info("ProductID:"+ productId + ", VWAP("+ averageMarketValue + ") < FairValue(" + fairValue + ")");
    }
  }

  public void handleTransactionOccured(String productId, long quantity, double price) {
    storeMarketValue(productId, quantity, price);
    
    Double marketValue = getAverageMarketValue(productId);

    //logger.info("VWAP for " + productId + " = " + marketValue);

    Double fairValue = getFairValue(productId);
    compareAndWriteResults(productId, fairValue, marketValue);

  }

  private void compareAndWriteResults(String productId, Double fairValue, Double marketValue) {
	  if (fairValue != null && marketValue !=null) {
	      int c = compareValues(fairValue, marketValue);
	      writeResults(logger, productId, fairValue, marketValue, c);
	      if ("TEST_PRODUCT".equals(productId))
	    	  writeResults(testProductLogger, productId, fairValue, marketValue, c);
	    } 
	
  }

  public  void handleFairValueChanged(String productId, double fairValue) {
    logFairValueToTestLogger(productId, fairValue);
    storeFairValue(productId, fairValue);

    Double marketValue = getAverageMarketValue(productId);
    
    compareAndWriteResults( productId,  fairValue,  marketValue) ;
  }
  
  private void storeFairValue(String productId, double fairValue) {
	  fairValueMap.put(productId, fairValue);
	  //logger.info("Fair Value for " + productId + " is " + fairValueMap.get(productId).toString());
	
  }

  private void logFairValueToTestLogger(String productId, double fairValue) {
	  if ("TEST_PRODUCT".equals(productId))
	      testProductLogger.debug("Fair Value for " + productId + " = " + fairValue);
	
  }

  public Double getFairValue(String productId) {
	  return fairValueMap.get(productId);
  }
  public MarketValueQueue getMarketData(String productId) {
	  return marketValueMap.get(productId);
  }

  public int compareValues(Double fairValue, Double averageMarketValue) {
    return Double.compare( averageMarketValue, fairValue);
  }

  public  void storeMarketValue(String productId, long quantity, double price) {
      logMarketValueToTestLogger(productId, quantity, price);
      MarketValueQueue queue = getMarketValueQueue(productId);
      try {

        queue.addItem(new MarketValueItem(productId, quantity, price));

      } catch (InterruptedException e) {
        logger.error("Exception in storeMarketValue : " +e.getMessage());
      }  
    
  }

  private MarketValueQueue getMarketValueQueue(String productId) {
	  MarketValueQueue queue = null;

        // check whether queue exist, if no , create it
        if (marketValueMap.get(productId) == null) { 
          queue = new MarketValueQueue();
          marketValueMap.put(productId, queue);
        } else {
          queue = marketValueMap.get(productId);
        }

	  return queue;
}

private void logMarketValueToTestLogger(String productId, long quantity, double price) {
	  if ("TEST_PRODUCT".equals(productId))
	        testProductLogger.debug("Market Value for " + productId + ", quantity = " + quantity + ", price =" + price);
	      
	
}

public Double getAverageMarketValue(String productId) {
      if (marketValueMap.get(productId) == null) return null;

      MarketValueQueue queue = marketValueMap.get(productId);
      if ( queue == null ) return null;

      return queue.getAverageMarketValue();

  }

  public VwapTrigger() {
    fairValueMap = new ConcurrentHashMap<String, Double>();
    marketValueMap = new ConcurrentHashMap<String, MarketValueQueue>();


  }

  public static void main(String[] args) {
    VwapTrigger vTrig = new VwapTrigger();

    DataProvider provider = DataProviderFactory.getDataProvider();
    provider.addMarketDataListener(new MarketDataListener() {
      public void transactionOccured(String productId, long quantity, double price) {
        // TODO Start to code here when a transaction occurred
        vTrig.handleTransactionOccured(productId, quantity, price);

      }
    });
    provider.addPricingDataListener(new PricingDataListener() {
      public void fairValueChanged(String productId, double fairValue) {
        // TODO Start to code here when a fair value changed
        // System.out.println("productId:"+ productId + ", fairValue:" + fairValue);
        vTrig.handleFairValueChanged(productId, fairValue);
        
      }
    });

    provider.listen();
    // When this method returns, the test is finished and you can check your results
  }
}