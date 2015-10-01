package server.client;

import javax.websocket.Session;

/**
 * Created by daleappleby on 1/10/15.
 */
public class Client {
    private final String name;
    private final Session session;
    private int rating;

    public Client(String name, Session session) {
        this.name = name;
        this.session = session;
    }

    public final String getClientSessionID(){
        return session.getId();
    }

    public final Session getSession(){
        return session;
    }
}
