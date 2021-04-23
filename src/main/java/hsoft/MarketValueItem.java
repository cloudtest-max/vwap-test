package hsoft;
public class MarketValueItem {
    private String productId;
    private long quantity;
    private double price;


    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public long getQuantity() {
        return quantity;
    }
    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
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