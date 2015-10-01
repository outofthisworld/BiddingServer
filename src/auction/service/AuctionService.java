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
import server.client.Client;

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


/**
 * The type Auction service.
 */
public class AuctionService {

    /*
     * An auction processor which takes care of processing finished auctions.
     */
    private final AuctionProcessor auctionProcessor = new AuctionProcessor();

    /*
    * A Concurrent HashMap which holds each of the currently running auctions along with their ID.
    */
    private final ConcurrentHashMap<Integer, Auction> currentlyRunningAuctions = new ConcurrentHashMap<>();

    /*
     * A scheduled executor service used for processing auctions. Should the executors thread fail
     * a new thread is created to take its place. Only one task may be running at any time on this executor.
     */
    private final ScheduledExecutorService auctionExecutorService = Executors.newSingleThreadScheduledExecutor();


    //Static initializer which is called once when a class is first initiated. Takes care of scheduling the thread to monitor auctions.
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

    /**
     * Check auction viability.
     *
     * @param <T>     the type parameter
     * @param auction the auction
     * @return the boolean
     */
    public <T extends Auction> boolean checkAuctionViability(T auction) {
        return (auction != null && auction.getAuctionItem() != null && auction.getTimeLeft() > 0);
    }

    /**
     * Add auction.
     *
     * @param <T>     the type parameter
     * @param auction the auction
     * @return the int
     */
    public <T extends Auction> int addAuction(T auction) {
        if (!checkAuctionViability(auction)) {
            return -1;
        }

        currentlyRunningAuctions.put(auction.getAuctionID(), auction);
        return auction.getAuctionID();
    }

    /**
     * Add auction.
     *
     * @param <T>              the type parameter
     * @param client           the client
     * @param auctionName      the auction name
     * @param item             the item
     * @param duration         the duration
     * @param durationTimeUnit the duration time unit
     * @param auctionType      the auction type
     * @return the int
     */
    public <T extends AuctionItem> int addAuction(Client client, String auctionName, T item, long duration, TimeUnit durationTimeUnit, AuctionType auctionType) {
        Auction auction = new Auction(client, auctionName, item, duration, durationTimeUnit, auctionType);
        if (!checkAuctionViability(auction)) {
            return -1;
        }

        currentlyRunningAuctions.put(auction.getAuctionID(), auction);
        return auction.getAuctionID();
    }

    /**
     * Add auction.
     *
     * @param client           the client
     * @param auctionName      the auction name
     * @param itemName         the item name
     * @param itemPrice        the item price
     * @param itemDescription  the item description
     * @param duration         the duration
     * @param durationTimeUnit the duration time unit
     * @param auctionType      the auction type
     * @return the int
     */
    public int addAuction(Client client, String auctionName, String itemName, double itemPrice, String itemDescription, long duration, TimeUnit durationTimeUnit, AuctionType auctionType) {
        Auction auction = new Auction(client, auctionName, new AuctionItem(itemName, itemPrice, itemDescription), duration, durationTimeUnit, auctionType);
        if (!checkAuctionViability(auction)) {
            return -1;
        }

        currentlyRunningAuctions.put(auction.getAuctionID(), auction);
        return auction.getAuctionID();
    }

    /**
     * Gets auction.
     *
     * @param auctionID the auction iD
     * @return the auction
     */
    public Optional<Auction> getAuction(int auctionID) {
        return Optional.ofNullable(currentlyRunningAuctions.get(auctionID));
    }

    /**
     * Remove auction.
     *
     * @param auctionID the auction iD
     */
    public void removeAuction(Integer auctionID) {
        currentlyRunningAuctions.remove(auctionID);
    }

    /**
     * Remove auction.
     *
     * @param <T>       the type parameter
     * @param <K>       the type parameter
     * @param auctionID the auction iD
     * @param auction   the auction
     * @return the boolean
     */
    public <T extends Number, K extends Auction> boolean removeAuction(T auctionID, K auction) {
        return currentlyRunningAuctions.remove(auctionID, auction);
    }

    /**
     * For each auction.
     *
     * @param auctionConsumer the auction consumer
     */
    public void forEachAuction(Consumer<? super Auction> auctionConsumer) {
        currentlyRunningAuctions.forEach((k, v) -> {
            if (v.isRunning()) {
                auctionConsumer.accept(v);
            }
        });
    }

    /**
     * For each auction.
     *
     * @param auctionConsumer the auction consumer
     */
    public void forEachAuction(BiConsumer<? super Number, ? super Auction> auctionConsumer) {
        currentlyRunningAuctions.forEach((k, v) -> {
            if (v.isRunning()) {
                auctionConsumer.accept(k, v);
            }
        });
    }

    /**
     * Filter auctions.
     *
     * @param predicate the predicate
     * @param consumer the consumer
     * @return the auction service
     */
    public AuctionService filterAuctions(Predicate<? super Auction> predicate, Consumer<? super Auction> consumer) {
        currentlyRunningAuctions.forEach((k, v) -> {
            if (predicate.test(v) && v.isRunning()) {
                consumer.accept(v);
            }
        });
        return this;
    }


    /**
     * Filter auctions.
     *
     * @param iAuctionFilter the i auction filter
     * @param object the object
     * @param consumer the consumer
     * @return the auction service
     */
    public AuctionService filterAuctions(IAuctionFilter iAuctionFilter, Object object, Consumer<? super Auction> consumer) {
        Predicate p = iAuctionFilter.filterFor(object);
        currentlyRunningAuctions.forEach((k, v) -> {
            if (p.test(v)) {
                consumer.accept(v);
            }
        });
        return this;
    }

    /**
     * Remove all auctions matching.
     *
     * @param predicate the predicate
     * @return the auction service
     */
    public AuctionService removeAllAuctionsMatching(Predicate<? super Auction> predicate) {
        for (Iterator<Entry<Integer, Auction>> it = currentlyRunningAuctions.entrySet().iterator(); it.hasNext();) {
            Entry<Integer, Auction> auctionEntry = it.next();
            if (predicate.test(auctionEntry.getValue())) {
                it.remove();
            }
        }
        return this;
    }

    /**
     * Immediately stop auction service.
     */
    public void immediatelyStopAuctionService() {
        if (!auctionServiceIsRunning()) {
            return;
        }

        auctionExecutorService.shutdownNow();
    }

    /**
     * Stop auction service.
     */
    public void stopAuctionService() {
        if (!auctionServiceIsRunning()) {
            return;
        }

        auctionExecutorService.shutdown();
    }

    /**
     * Auction service is running.
     *
     * @return the boolean
     */
    public boolean auctionServiceIsRunning() {
        return !(auctionExecutorService == null || auctionExecutorService.isShutdown());
    }

    /**
     * Stop auction service.
     *
     * @param awaitTermination the await termination
     * @param timeUnit the time unit
     */
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
