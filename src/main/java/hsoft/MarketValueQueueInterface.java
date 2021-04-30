package hsoft;

import java.util.List;

public interface MarketValueQueueInterface {
	   
	    public void put(MarketValueItem e)
	        throws InterruptedException ;
	    public MarketValueItem take()
	       throws InterruptedException ;
	    public Double getAverageMarketValue()  ;
	    public void addItemAndRemoveTail(MarketValueItem item) throws InterruptedException ;
	    public List<MarketValueItem> getLast5Data();
}
