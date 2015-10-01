package server.client;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Created by daleappleby on 1/10/15.
 */
public class ClientHandler {
    //Holds all clients connected to the server.
    private ConcurrentHashMap<String, Client> clients = new ConcurrentHashMap<>();

    /**
     * Instantiates a new Client handler.
     */
    public ClientHandler() {}

    public void addClient(Session session) {
        clients.put(session.getId(), new Client(session));
    }

    /**
     * Add client.
     *
     * @param <T>    the type parameter
     * @param client the client
     */
    public <T extends Client> void addClient(T client) {
        clients.put(client.getClientSessionID(), client);
    }

    /**
     * Remove client.
     *
     * @param <T>             the type parameter
     * @param clientSessionID the client session iD
     */
    public <T extends Client> void removeClient(String clientSessionID) {
        clients.remove(clientSessionID);
    }

    /**
     * For each session.
     *
     * @param consumer the consumer
     */
    public void forEachSession(Consumer<? super Client> consumer) {
        for (Iterator<Client> it = clients.values().iterator(); it.hasNext(); ) {
                Client sess = it.next();
            if (sess.getSession().isOpen()) {
                    consumer.accept(sess);
            } else {
                    try {
                        sess.getSession().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    it.remove();
                }
            }
    }
}
