package auction.service;

import auction.Auction;
import utilities.Preconditions;

import java.util.function.Predicate;


/**
 * The enum Auction filters.
 *
 * This enum represents convenience methods for filtering auctions,
 * returning the predicates required to do common auction-filtering tasks.
 *
 * Objects supplied to the implemented {@link #filterFor(Object object);} via the {@link auction.service.IAuctionFilter}
 * should not not be null.
 */
public enum AuctionFilters implements IAuctionFilter {
    /**
     * The AUCTION_ID.
     */
    AUCTION_ID() {
        @Override
        public Predicate<? super Auction> filterFor(Object object) {
            Preconditions.checkNotNull(this.getClass().getName(), object);
            return (t) -> {
                return Integer.valueOf(t.getAuctionID()) == object;
            };
        }
    },

    /**
     * A predicate used to determine if an auction is or is not running.
     * The supplied object should be of boolean type specifying whether or not the
     * {@link Auction#isRunning()} method of the {@link auction.Auction} class should be true or false.
     */
    IS_RUNNING() {
        @Override
        public Predicate<? super Auction> filterFor(Object object) {
            Preconditions.checkNotNull(this.getClass().getName(), object);
            return (t) -> {
                return ((Boolean) t.isRunning()) == object;
            };
        }
    },
    /**
     * The HIGHEST_BIDDER.
     */
    HIGHEST_BIDDER() {
        @Override
        public Predicate<? super Auction> filterFor(Object object) {
            Preconditions.checkNotNull(this.getClass().getName(), object);
            return (t) -> {
                return t.getHighestBidder() == object;
            };
        }
    },

    AUCTION_TYPE() {
        @Override
        public Predicate<? super Auction> filterFor(Object object) {
            return (t) -> {
                return t.getAuctionType() == object;
            };
        }
    }

}
