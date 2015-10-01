package auction.service;

import auction.Auction;

import java.util.function.Predicate;

/**
 * Created by daleappleby on 1/10/15.
 */
public interface IAuctionFilter {
    public Predicate<? super Auction> filterFor(Object object);
}

