package hsoft;

import java.util.List;

public interface MarketValueQueueInterface {
	   
	    public MarketValueItem peekLast()
	       throws InterruptedException;
	    public Double getAverageMarketValue()  ;
	    public void addItemAndRemoveTail(MarketValueItem item) throws InterruptedException ;
	    public List<MarketValueItem> getLast5Data();
}
