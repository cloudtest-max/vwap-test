package hsoft;
public class MarketValueItem {
    private final String productId;
    private final long quantity;
    private final double price;


    public String getProductId() {
        return productId;
    }
    
    public long getQuantity() {
        return quantity;
    }
    
    public double getPrice() {
        return price;
    }
    

    //Constructor
    public MarketValueItem(String productId, long quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }
    
    public String toString() {
    	return "(productId="+ productId + ", quantity="+ quantity + ", price="+ price + ")";
    }

}