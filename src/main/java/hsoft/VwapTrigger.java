package hsoft;

import com.hsoft.codingtest.DataProvider;
import com.hsoft.codingtest.DataProviderFactory;
import com.hsoft.codingtest.MarketDataListener;
import com.hsoft.codingtest.PricingDataListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;


public class VwapTrigger {
  private static final Logger logger = Logger.getLogger(VwapTrigger.class);
  private static final Logger testProductLogger  = Logger.getLogger("TEST_PRODUCT");

  private final Map<String, Double> fairValueMap = new ConcurrentHashMap<String, Double>();
  private final Map<String, MarketValueQueueInterface> marketValueMap = new ConcurrentHashMap<String, MarketValueQueueInterface>();
  
  private void writeResults(Logger logger, String productId, Double fairValue, Double averageMarketValue, int result) {

    if (result > 0) {
      logger.info("ProductID:"+ productId + ", VWAP("+ averageMarketValue + ") > FairValue(" + fairValue + ")");
    } else if (result == 0) {
      logger.info("ProductID:"+ productId + ", VWAP("+ averageMarketValue + ") = FairValue(" + fairValue + ")");
    } else if (result < 0) {
      logger.info("ProductID:"+ productId + ", VWAP("+ averageMarketValue + ") < FairValue(" + fairValue + ")");
    }
  }

  public void handleTransactionOccured(String productId, long quantity, double price) {
	long startTime= System.nanoTime();
    storeMarketValue(productId, quantity, price);
    
    Double marketValue = getAverageMarketValue(productId);

    Double fairValue = getFairValue(productId);
    compareAndWriteResults(productId, fairValue, marketValue);
    
    long elapsedTime= (System.nanoTime() - startTime)/1000;
    String timeMsg = "handleTransactionOccured: productId: " + productId + " processing time:" + (elapsedTime);
    logger.info(timeMsg);
    logStringToTestLogger(productId, timeMsg);

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
	long startTime= System.nanoTime();
	logFairValueToTestLogger(productId, fairValue);
    storeFairValue(productId, fairValue);

    Double marketValue = getAverageMarketValue(productId);
    
    compareAndWriteResults( productId,  fairValue,  marketValue) ;
    long elapsedTime= (System.nanoTime() - startTime)/1000;
    String timeMsg = "handleFairValueChanged: productId: " + productId + " processing time:" + (elapsedTime);
    logger.info(timeMsg);
    logStringToTestLogger(productId, timeMsg);
  }
  
  private void storeFairValue(String productId, double fairValue) {
	  fairValueMap.put(productId, fairValue); // use of conncurrentHashMap to guarantee thread-safety
	  
	
  }

  private void logFairValueToTestLogger(String productId, double fairValue) {
	  if ("TEST_PRODUCT".equals(productId))
	      testProductLogger.debug("Fair Value for " + productId + " = " + fairValue);
	
  }
  
  private void logStringToTestLogger(String productId, String stringValue) {
	  if ("TEST_PRODUCT".equals(productId))
	      testProductLogger.debug(stringValue);
	
  }

  protected Double getFairValue(String productId) {
	  return fairValueMap.get(productId);
  }

  private int compareValues(Double fairValue, Double averageMarketValue) {
    return Double.compare( averageMarketValue, fairValue);
  }

  private  void storeMarketValue(String productId, long quantity, double price) {
      logMarketValueToTestLogger(productId, quantity, price);
      MarketValueQueueInterface queue = getMarketValueQueueAndCreateIfApplicable(productId);
      try {

        queue.addItemAndRemoveTail(new MarketValueItem(productId, quantity, price));

      } catch (InterruptedException e) {
        logger.error("Exception in storeMarketValue : " +e.getMessage());
      }  
    
  }

  private MarketValueQueueInterface getMarketValueQueueAndCreateIfApplicable(String productId) {
	  MarketValueQueueInterface queue = null;

        // check whether queue exist, if no , create it
        if (marketValueMap.get(productId) == null) { 
          synchronized (marketValueMap) { // to make sure no 2 thread create queue for the same productId
        	  if (marketValueMap.get(productId) ==null) {
		          queue = new MarketValueQueue3(); //change to use ConcurrentLinkedDeque
		          
		          marketValueMap.put(productId, queue);
        	  } else
        		  queue = marketValueMap.get(productId);
        	  
          }
        } else {
          queue = marketValueMap.get(productId);
        }

	  return queue;
  }

  private void logMarketValueToTestLogger(String productId, long quantity, double price) {
	  if ("TEST_PRODUCT".equals(productId))
	        testProductLogger.debug("Market Value for " + productId + ", quantity = " + quantity + ", price =" + price);
	      
	
  }

  protected Double getAverageMarketValue(String productId) {
      if (marketValueMap.get(productId) == null) return null;

      MarketValueQueueInterface queue = marketValueMap.get(productId); // the marketValueMap use ConcurrentHashMap to guarantee thread-safety
      if ( queue == null ) return null;

      return queue.getAverageMarketValue();

  }

  public VwapTrigger() {
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
  

  // used by testing class only
  protected MarketValueQueueInterface getMarketData(String productId) {
	  return marketValueMap.get(productId);
  }

}
