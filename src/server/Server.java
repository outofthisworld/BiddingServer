package server;

import auction.service.AuctionFilters;
import auction.service.AuctionService;
import server.client.ClientHandler;
/**
 * Created by daleappleby on 1/10/15.
 */


public class Server {

    //The client handler shared by all instances of this server.
    public static final ClientHandler CLIENT_HANDLER = new ClientHandler();

    //The auction service shared by all instances of this server
    public static final AuctionService AUCTION_SERVICE = new AuctionService();

    public Server() {
        AUCTION_SERVICE.filterAuctions(AuctionFilters.AUCTION_ID.filterFor(1), (a) -> {
        });
    }

}
