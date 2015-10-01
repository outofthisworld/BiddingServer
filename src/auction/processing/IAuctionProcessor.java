package auction.processing;

import auction.Auction;

import java.util.concurrent.Callable;

/**
 * Created by daleappleby on 1/10/15.
 */
public interface IAuctionProcessor {
    public <T extends Auction> void processAsync(T auction, Callable<? extends AuctionProcessorResponse> callable);
}
