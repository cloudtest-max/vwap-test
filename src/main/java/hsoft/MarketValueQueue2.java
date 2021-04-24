package hsoft;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.*;

public class MarketValueQueue2 implements MarketValueQueueInterface {
    private LinkedBlockingDeque<MarketValueItem> backingQueue;
    public static final int QUEUE_SIZE = 10;
    public static final int QUEUE_SIZE_FOR_REMOVE = 8;

    public MarketValueQueue2() {
    	
        backingQueue = new LinkedBlockingDeque<MarketValueItem>(QUEUE_SIZE);
    }

    public int size() {
        return backingQueue.size();
    }
    public void put(MarketValueItem e)
        throws InterruptedException {
            backingQueue.put(e);
    }
    public MarketValueItem take()
       throws InterruptedException {
        return backingQueue.take();
    }
    public Double getAverageMarketValue()  {
        //get the last 5 price
      int count = 0;
      double sum = 0;
      long denominator = 0;
      Double price = null;
      synchronized(backingQueue) {
        Iterator<MarketValueItem> it = backingQueue.descendingIterator();
        while (it.hasNext() && count <5) {
            MarketValueItem item = it.next();
            sum += (item.getPrice() * item.getQuantity());
            denominator += item.getQuantity();
            count++;
        }
        if (denominator > 0)
            price = new Double(sum/denominator);
      }
      return price;
    }
    public void addItemAndRemoveTail(MarketValueItem item) throws InterruptedException {
        synchronized(backingQueue) {
            // if queue is full, remove the tail and insert the new element
            if (backingQueue.size() >= MarketValueQueue2.QUEUE_SIZE_FOR_REMOVE) {
                backingQueue.take();
            }
                
            backingQueue.put(item);
        }
    }
    public List<MarketValueItem>  getLast5Data() {
    	List<MarketValueItem> list = new ArrayList<MarketValueItem>();
    	int count=0;
    	synchronized(backingQueue) {
            Iterator<MarketValueItem> it = backingQueue.descendingIterator();
            while (it.hasNext() && count <5) {
                MarketValueItem item = it.next();
                list.add(item);
                count++;
            }
            
          }
    	return list;
    }
}