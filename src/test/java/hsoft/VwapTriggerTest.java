package hsoft;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;


// One may want to add some automatic test(s) here
public class VwapTriggerTest {
  private static VwapTrigger vwapTrigger = null;
  @Test
  void shouldXxxx() {
    assertEquals(1, 1);
  }
  
  @Test
  void shouldGetSameFairValueAfterStore() {
	  vwapTrigger = new VwapTrigger();
	  
	  String productId = "TEST_PRODUCT";
	  Double fairValue = Double.valueOf(200);
	  vwapTrigger.handleFairValueChanged(productId, fairValue);
	  assertTrue(Double.compare(fairValue, vwapTrigger.getFairValue(productId))==0);
	  
	  vwapTrigger = null;
  }
  
  @Test
  void shouldGetSameMarketValueAfterStore() {
	  vwapTrigger = new VwapTrigger();
	  
	  String productId = "TEST_PRODUCT";
	  long quantity = 400;
	  Double marketValue = Double.valueOf(200);
	  vwapTrigger.handleTransactionOccured(productId, quantity, marketValue);
	  MarketValueItem item = null;
		try {
			item = vwapTrigger.getMarketData(productId).peekLast();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
	  assertTrue(Double.compare(marketValue, item.getPrice())==0);
	  assertTrue(Double.compare(quantity, item.getQuantity())==0);
	  
	  vwapTrigger = null;
  }
  
  @Test
  void shouldGetLast5MarketData() {
	  vwapTrigger = new VwapTrigger();
	  
	  String productId = "TEST_PRODUCT";
	  long  quantity[] = { 400, 500, 600, 100, 200, 300, 400 };
	  int no_of_item = quantity.length;
	  
	  double marketValue[] = { 200.0, 300.0, 400.0, 600.0, 50.0, 70.0, 80.0 };
	  for (int i=0; i< 7; i++) {
		  vwapTrigger.handleTransactionOccured(productId, quantity[i], marketValue[i]);
	  }
	  
	  
	  MarketValueItem item = null;
		List<MarketValueItem> list =  vwapTrigger.getMarketData(productId).getLast5Data();
		  for (int j=0; j< 5 ; j++) {
			  item = list.get(j);
			  //item = vwapTrigger.getMarketData(productId).take();
			  int index = no_of_item - 1 - j;
			  System.out.println(item + ", against marketValue="+marketValue[index]+",quantity="+quantity[index]);
			  assertTrue(Double.compare(marketValue[index], item.getPrice())==0);
			  assertTrue(Double.compare(quantity[index], item.getQuantity())==0);
		  }
	  
	  
	  vwapTrigger = null;
  }
  
  @Test
  void shouldGetAverageMarketValue() {
	  vwapTrigger = new VwapTrigger();
	  
	  String productId = "TEST_PRODUCT";
	  long  quantity[] = { 400, 500, 600, 100, 200, 300, 400 };
	  int no_of_item = quantity.length;
	  
	  double marketValue[] = { 200.0, 300.0, 400.0, 600.0, 50.0, 70.0, 80.0 };
	  for (int i=0; i< 7; i++) {
		  vwapTrigger.handleTransactionOccured(productId, quantity[i], marketValue[i]);
	  }
	  
	  
	  double sum = 0.0;
	  long denominator = 0;
	  for (int j=0; j<5; j++) {
		  int index = no_of_item - 5 +j;
		  sum += (marketValue[index] * quantity[index]);
		  denominator += quantity[index];
		  
		  
	  }	  
	  
	  Double expectedAveragePrice = null;
	  if (denominator >=0) 
		  expectedAveragePrice = sum/denominator;
	  
	  MarketValueItem item = null;
		
			  Double averagePrice = vwapTrigger.getAverageMarketValue(productId);
			  System.out.println("Comparing " + expectedAveragePrice + " with " + averagePrice);
			  assertTrue(Double.compare(expectedAveragePrice, averagePrice)==0);
			  
		
	  
	  
	  vwapTrigger = null;
  }
  
//  @Test
//  void shouldPass() {
//	  
//	  
//	  vwapTrigger.handleTransactionOccured(productId, quantity, price);
//	  
//	  vwapTrigger.storeFairValue(productId, fairValue);
//  }
  

  
}