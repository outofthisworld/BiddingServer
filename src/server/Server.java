package server;

import auction.service.AuctionService;
import server.client.ClientHandler;

/**
 * Created by daleappleby on 1/10/15.
 */
public class Server {
    public static final ClientHandler CLIENT_HANDLER = new ClientHandler();
    public static final AuctionService AUCTION_SERVICE = new AuctionService();

    public Server() {

    }

}