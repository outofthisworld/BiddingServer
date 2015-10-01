package auction.processing;

import auction.Auction;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by daleappleby on 1/10/15.
 */
public class AuctionProcessor implements IAuctionProcessor {
    private static Logger logger = Logger.getLogger(AuctionProcessor.class.getName());
    private final ExecutorService auctionProcessor = Executors.newCachedThreadPool(buildThreadFactory());
    private AuctionHandler auctionHandler;

    public AuctionProcessor() {

    }

    public <T extends Auction> void processAsync(T auction, Callable<? extends AuctionProcessorResponse> callable) {
        Objects.requireNonNull(auction,"Auction supplied to auction processor was null");
        auctionProcessor.execute(()->{
            try {
                AuctionProcessorResponse result = callable.call();
                onComplete(result,auction);
            } catch (Exception e) {
                onIneligibleForProcessing(new AuctionProcessorResponse(AuctionProcessorResponse.Response.COULD_NOT_COMPUTE),auction);
                e.printStackTrace();
            }
        });
    }

    protected <T extends Auction> void onComplete(AuctionProcessorResponse response,T auction) {
        if(auctionHandler != null)
            auctionHandler.onComplete(response,auction);

        //default behaviour for when an auction is processed.
    }


    protected <T extends Auction> void onIneligibleForProcessing(AuctionProcessorResponse auctionProcessorResponse,T auction) {
        if(auctionHandler != null)
            auctionHandler.onError(auctionProcessorResponse, auction);

        //Default behaviour when auction cannot be processed.
    }

    public void setAuctionHandler(AuctionHandler auctionHandler){
        this.auctionHandler = auctionHandler;
    }

    private static final ThreadFactory buildThreadFactory(){
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setPriority(10);
                thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        logger.log(Level.SEVERE,"Exception in thread {0}",t.getId() + ":" + t.getName());
                        logger.log(Level.SEVERE,"Exception in thread {0}",e);
                    }
                });
                return thread;
            }
        };
    }
}
