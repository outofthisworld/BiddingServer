/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auction.service;

import auction.Auction;
import auction.AuctionItem;
import auction.AuctionType;
import auction.processing.AuctionProcessor;
import auction.processing.AuctionProcessorResponse;
import server.Server;
import javax.websocket.Session;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AuctionService {
    private final AuctionProcessor auctionProcessor = new AuctionProcessor();
    private final ConcurrentHashMap<Integer, Auction> currentlyRunningAuctions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService auctionExecutorService = Executors.newSingleThreadScheduledExecutor();

    {
        auctionExecutorService.scheduleAtFixedRate(() -> {
            long startTime = System.currentTimeMillis();
            for (Iterator<Entry<Integer, Auction>> it = currentlyRunningAuctions.entrySet().iterator(); it.hasNext();) {
                Auction auction = it.next().getValue();
                //If the auction duration is above 0 (ie not finished)
                if (auction.getTimeLeft() > 0) {
                    auction.decrementDuration();
                    auction.broadcastAsyncMessageToViewers("time:" + auction.getTimeLeft());
                    Server.CLIENT_HANDLER.forEachSession((Session sess) -> {
                        sess.getAsyncRemote().sendText("time:" + auction.getAuctionID() + ":" + auction.getTimeLeft());
                    });
                } else {
                    //The auction has come to an end, remove it from the HashMap.
                    it.remove();
                    auction.setRunning(false);
                    auctionProcessor.processAsync(auction,()->{
                        //Process the finished auction
                        return new AuctionProcessorResponse(AuctionProcessorResponse.Response.COMPLETED);
                    });
                }
            }
            long timeTaken = System.currentTimeMillis() - startTime;
        }, 1, 1, TimeUnit.SECONDS);
    }

    public <T extends Auction> boolean checkAuctionViability(T auction) {
        return (auction != null && auction.getAuctionItem() != null && auction.getTimeLeft() > 0);
    }

    public <T extends Auction> int addAuction(T auction) {
        if (!checkAuctionViability(auction)) {
            return -1;
        }

        currentlyRunningAuctions.put(auction.getAuctionID(), auction);
        return auction.getAuctionID();
    }

    public <T extends AuctionItem> int addAuction(String auctionName, T item, long duration, TimeUnit durationTimeUnit, AuctionType auctionType) {
        Auction auction = new Auction(auctionName, item, duration, durationTimeUnit,auctionType);
        if (!checkAuctionViability(auction)) {
            return -1;
        }

        currentlyRunningAuctions.put(auction.getAuctionID(), auction);
        return auction.getAuctionID();
    }

    public int addAuction(String auctionName, String itemName, double itemPrice,String itemDescription, long duration, TimeUnit durationTimeUnit,AuctionType auctionType) {
        Auction auction = new Auction(auctionName, new AuctionItem(itemName, itemPrice,itemDescription), duration, durationTimeUnit,auctionType);
        if (!checkAuctionViability(auction)) {
            return -1;
        }

        currentlyRunningAuctions.put(auction.getAuctionID(), auction);
        return auction.getAuctionID();
    }

    public Optional<Auction> getAuction(int auctionID) {
        return Optional.ofNullable(currentlyRunningAuctions.get(auctionID));
    }

    public void removeAuction(Integer auctionID) {
        currentlyRunningAuctions.remove(auctionID);
    }

    public <T extends Number, K extends Auction> boolean removeAuction(T auctionID, K auction) {
        return currentlyRunningAuctions.remove(auctionID, auction);
    }

    public void forEachAuction(Consumer<? super Auction> auctionConsumer) {
        currentlyRunningAuctions.forEach((k, v) -> {
            if (v.isRunning()) {
                auctionConsumer.accept(v);
            }
        });
    }

    public void forEachAuction(BiConsumer<? super Number, ? super Auction> auctionConsumer) {
        currentlyRunningAuctions.forEach((k, v) -> {
            if (v.isRunning()) {
                auctionConsumer.accept(k, v);
            }
        });
    }

    public AuctionService filterAuctions(Predicate<? super Auction> predicate, Consumer<? super Auction> consumer) {
        currentlyRunningAuctions.forEach((k, v) -> {
            if (predicate.test(v) && v.isRunning()) {
                consumer.accept(v);
            }
        });
        return this;
    }

    public AuctionService filterAuctions(Predicate<? super Auction> predicate, BiConsumer<? super Number, ? super Auction> consumer) {
        currentlyRunningAuctions.forEach((k, v) -> {
            if (predicate.test(v)) {
                consumer.accept(k, v);
            }
        });
        return this;
    }

    public AuctionService removeAllAuctionsMatching(Predicate<? super Auction> predicate) {
        for (Iterator<Entry<Integer, Auction>> it = currentlyRunningAuctions.entrySet().iterator(); it.hasNext();) {
            Entry<Integer, Auction> auctionEntry = it.next();
            if (predicate.test(auctionEntry.getValue())) {
                it.remove();
            }
        }
        return this;
    }

    public void immediatelyStopAuctionService() {
        if (!auctionServiceIsRunning()) {
            return;
        }

        auctionExecutorService.shutdownNow();
    }

    public void stopAuctionService() {
        if (!auctionServiceIsRunning()) {
            return;
        }

        auctionExecutorService.shutdown();
    }

    public boolean auctionServiceIsRunning() {
        return !(auctionExecutorService == null || auctionExecutorService.isShutdown());
    }

    public void stopAuctionService(long awaitTermination, TimeUnit timeUnit) {
        if (!auctionServiceIsRunning()) {
            return;
        }

        auctionExecutorService.shutdown();
        try {
            auctionExecutorService.awaitTermination(awaitTermination, timeUnit);
        } catch (InterruptedException ex) {
            Logger.getLogger(AuctionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
