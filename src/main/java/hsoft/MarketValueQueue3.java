package hsoft;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

// use non blocking deque as implementation to improve performance
public class MarketValueQueue3 implements MarketValueQueueInterface {
    private final ConcurrentLinkedDeque<MarketValueItem> backingQueue;
    public static final int QUEUE_SIZE_TO_TRIGGER_REMOVE = 15;
    public static final int QUEUE_SIZE_TO_KEEP = 8;
    private AtomicInteger count = new AtomicInteger(0);

    public MarketValueQueue3() {
    	
        backingQueue = new ConcurrentLinkedDeque<MarketValueItem>();
    }

    public MarketValueItem peekLast()
    	       throws InterruptedException {
    	        return backingQueue.peekLast();
    }
    public Double getAverageMarketValue()  {
        //get the last 5 price
      int count = 0;
      double sum = 0;
      long denominator = 0;
      Double price = null;
        Iterator<MarketValueItem> it = backingQueue.descendingIterator();
        while (it.hasNext() && count <5) {
            MarketValueItem item = it.next();
            sum += (item.getPrice() * item.getQuantity());
            denominator += item.getQuantity();
            count++;
        }
        if (denominator > 0)
            price = new Double(sum/denominator);
      return price;
    }
    public void addItemAndRemoveTail(MarketValueItem item) throws InterruptedException {

    	backingQueue.add(item);
    	int current_count = count.incrementAndGet();
        
        if ((current_count) > QUEUE_SIZE_TO_TRIGGER_REMOVE)
        	removeExcessElements();
        
        
    }
    public void removeExcessElements() throws InterruptedException {
    	//int countTmp = count.get();
    	while (count.get() > QUEUE_SIZE_TO_KEEP) {
    		count.decrementAndGet();
    		if (backingQueue.poll() == null) 
    			count.incrementAndGet();
    		
    	}
    	
    		
    }
    public List<MarketValueItem>  getLast5Data() {
    	List<MarketValueItem> list = new ArrayList<MarketValueItem>();
    	int count=0;

            Iterator<MarketValueItem> it = backingQueue.descendingIterator();
            while (it.hasNext() && count <5) {
                MarketValueItem item = it.next();
                list.add(item);
                count++;
            }
    	return list;
    }
}