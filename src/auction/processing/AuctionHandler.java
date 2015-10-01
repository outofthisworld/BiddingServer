package auction.processing;

import auction.Auction;

/**
 * Created by daleappleby on 1/10/15.
 */
public interface AuctionHandler {
    /**
     * On error.
     *
     * @param <T>  the type parameter
     * @param auctionProcessorResponse the auction processor response
     * @param auction the auction
     */
    public <T extends Auction> void onError(AuctionProcessorResponse auctionProcessorResponse,T auction);

    /**
     * On complete.
     *
     * @param <T>  the type parameter
     * @param auctionProcessorResponse the auction processor response
     * @param auction the auction
     */
    public <T extends Auction> void onComplete(AuctionProcessorResponse auctionProcessorResponse,T auction);
}
