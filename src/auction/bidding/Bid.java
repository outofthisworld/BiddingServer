package auction.bidding;

import server.client.Client;

import java.util.Date;


/**
 * A immutable class which represents a bid on an auction.
 */
public final class Bid {
    private final double bidAmount;
    private final Client biddingClient;
    private final Date dateOfBid;

    /**
     * Instantiates a new Bid.
     *
     * @param biddingClient the bidding client
     * @param bidAmount the bid amount
     * @param dateOfBid the date of bid
     */
    public Bid(Client biddingClient,double bidAmount,Date dateOfBid) {
        this.bidAmount = bidAmount;
        this.biddingClient = biddingClient;
        this.dateOfBid = dateOfBid;
    }

    /**
     * Gets bid amount.
     *
     * @return the bid amount
     */
    public double getBidAmount() {
        return bidAmount;
    }

    /**
     * Gets bidding client.
     *
     * @return the bidding client
     */
    public Client getBiddingClient() {
        return biddingClient;
    }

    /**
     * Gets date of bid.
     *
     * @return the date of bid
     */
    public Date getDateOfBid() {
        return dateOfBid;
    }
}
