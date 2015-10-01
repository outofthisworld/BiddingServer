package auction.service;

import Util.Preconditions;
import auction.Auction;

import java.util.function.Predicate;

/**
 * Created by daleappleby on 1/10/15.
 */
public enum AuctionFilters implements IAuctionFilter {

    AUCTION_ID() {
        @Override
        public Predicate<? super Auction> filterFor(Object object) {
            Preconditions.checkNotNull(this.getClass().getName(), object);
            return (t) -> {
                return Integer.valueOf(t.getAuctionID()) == object;
            };
        }
    },

    IS_RUNNING() {
        @Override
        public Predicate<? super Auction> filterFor(Object object) {
            Preconditions.checkNotNull(this.getClass().getName(), object);
            boolean checkFor = Preconditions.tryCast(boolean.class, object);
            return (t) -> {
                return t.isRunning() == checkFor;
            };
        }
    },
    S_S() {
        @Override
        public Predicate<? super Auction> filterFor(Object object) {
            return (t) -> {
                return t.getHighestBidder() == object;
            };
        }
    }


}
