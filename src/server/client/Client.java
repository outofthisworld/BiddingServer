package server.client;

import javax.websocket.Session;

/**
 * Created by daleappleby on 1/10/15.
 */
public class Client {
    private final Session session;

    public Client(Session session) {
        this.session = session;
    }

    public final String getClientSessionID(){
        return session.getId();
    }

    public final Session getSession(){
        return session;
    }
}
