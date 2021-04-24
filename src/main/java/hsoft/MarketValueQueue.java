package hsoft;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.*;

public class MarketValueQueue {
    private ArrayBlockingQueue<MarketValueItem> backingQueue;
    public static final int QUEUE_SIZE = 5;

    public MarketValueQueue() {
        backingQueue = new ArrayBlockingQueue<MarketValueItem>(QUEUE_SIZE);
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
        Iterator<MarketValueItem> it = backingQueue.iterator();
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
            if (backingQueue.size() >= MarketValueQueue.QUEUE_SIZE) {
                backingQueue.take();
            }
                
            backingQueue.put(item);
        }
    }
}