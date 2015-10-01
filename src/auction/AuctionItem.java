package auction;


/**
 * A immutable class which represents an AuctionItem.
 */
public final class AuctionItem {
    private final String itemName;
    private final double itemPrice;
    private final String description;
    
    public AuctionItem(String itemName, double itemPrice, String description){
        this.itemName=itemName;
        this.itemPrice=itemPrice;
        this.description = description;
    }

    public final String getItemName(){
        return itemName;
    }

    public final double getItemPrice(){
        return itemPrice;
    }

    public final String getItemDescription(){
        return description;
    }
}
