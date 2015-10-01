package auction;

import auction.bidding.Bid;
import server.client.Client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Dale Appleby on 28/09/15.
 */
public final class Auction {

    /**
     * Instantiates a new Auction.
     *
     * @param client the client
     * @param auctionTitle the auction title
     * @param auctionItem the auction item
     * @param duration the duration
     * @param timeUnit the time unit
     * @param auctionType the auction type
     */
    public Auction(Client client,String auctionTitle, AuctionItem auctionItem, long duration, TimeUnit timeUnit,AuctionType auctionType){
        this.client = client;
        this.isRunning = true;
        this.auctionTitle = auctionTitle;
        this.auctionItem = auctionItem;
        this.duration = TimeUnit.SECONDS.convert(duration,timeUnit);
        this.auctionID = surrogateAuctionID.incrementAndGet();
        this.auctionType = auctionType;
    }

    /**
     * Get auction iD.
     *
     * @return the int
     */
    public int getAuctionID(){
        return auctionID;
    }

    /**
     * Get auction title.
     *
     * @return the string
     */
    public String getAuctionTitle(){
        return auctionTitle;
    }

    /**
     * New viewer.
     *
     * @param client the client
     */
    public void newViewer(Client client){
        auctionSessions.add(client);
    }

    /**
     * Remove viewer.
     *
     * @param client the client
     */
    public void removeViewer(Client client){
        auctionSessions.remove(client);
    }

    /**
     * Get highest bidder.
     *
     * @return the client
     */
    public Client getHighestBidder(){
        return auctionBids.peek().getBiddingClient();
    }

    /**
     * Get highest bid.
     *
     * @return the double
     */
    public double getHighestBid(){
        return auctionBids.peek().getBidAmount();
    }

    /**
     * Add new bidder.
     *
     * @param bid the bid
     * @return the boolean
     */
    public boolean addNewBidder(Bid bid){
        if(bid.getBidAmount() <= getHighestBid() || duration <= 0) //Bid must be more than current bid
            return false;

        addBidHistory(bid);

        synchronized(this){
            bidCount++;
        }
        return true;
    }

    /**
     * Add bid history.
     *
     * @param <T>  the type parameter
     * @param bid the bid
     */
    public <T extends Bid> void addBidHistory(T bid){
        auctionBids.push(bid);
        //Send message to users
    }

    /**
     * Get bid count.
     *
     * @return the int
     */
    public synchronized int getBidCount(){
        return bidCount;
    }

    /**
     * Broadcast async message to viewers.
     *
     * @param message the message
     */
//Change to iterator to remove whilst iterating
    public void broadcastAsyncMessageToViewers(String message){
        synchronized(auctionSessions) {
            Set<Client> closedSessions = new HashSet();
            auctionSessions.stream().forEach(session -> {
                if(session.getSession().isOpen()){
                session.getSession().getAsyncRemote().sendText(message);
                }else{
                   closedSessions.add(session);
                }
            });
            auctionSessions.removeAll(closedSessions);
        }
    }

    /**
     * Get auction item name.
     *
     * @return the string
     */
    public String getAuctionItemName(){
        return auctionItem.getItemName();
    }

    /**
     * Get auction item price.
     *
     * @return the double
     */
    public double getAuctionItemPrice(){
        return auctionItem.getItemPrice();
    }

    /**
     * Get time left.
     *
     * @return the long
     */
    public long getTimeLeft(){
        return duration;
    }

    /**
     * Decrement duration.
     */
    public void decrementDuration(){
        duration--;
    }

    /**
     * Set running.
     *
     * @param value the value
     */
    public void setRunning(boolean value){
        isRunning = value;
    }

    /**
     * Is running.
     *
     * @return the boolean
     */
    public boolean isRunning(){
        return isRunning;
    }

    /**
     * Get auction item.
     *
     * @return the auction item
     */
    public AuctionItem getAuctionItem(){
        return auctionItem;
    }

    //The client this auction belongs to (may or may not be connected)
    private Client client;

    //The type of this auction.
    private AuctionType auctionType;

    //Specifies whether or not this auction is running
    private volatile boolean isRunning;

    //The duration, represented in seconds, that this auction should run for.
    private volatile long duration;

    //The title of this auction
    private final String auctionTitle;

    //An static autoIncrementing id used to identify each auction
    private static final AtomicInteger surrogateAuctionID = new AtomicInteger();

    //The id of this auction, obtained from the surrogate auction id.
    private final int auctionID;

    //The bid count of this auction
    private int bidCount = 0;

    //The auction item belonging to this auction
    private final AuctionItem auctionItem;

    //The set of clients viewing this auction
    private final Set<Client> auctionSessions = Collections.synchronizedSet(new HashSet());

    //The bids on this auction
    private final Stack<Bid> auctionBids = new Stack<>();
}
