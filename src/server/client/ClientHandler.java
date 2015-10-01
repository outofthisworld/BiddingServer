package server.client;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by daleappleby on 1/10/15.
 */
public class ClientHandler {
    private Set<Client> clients = Collections.synchronizedSet(new HashSet<Client>());

    public ClientHandler() {}

    public <T extends Client> boolean addClient(T client){
        return clients.add(client);
    }

    public <T extends Client> boolean removeClient(T client){
        return clients.remove(client);
    }

    public void forEachSession(Consumer<? super Client> consumer){
        synchronized(clients){
            //Iterator allows removal whilst iterating
            for(Iterator<Client> it=clients.iterator();it.hasNext();){
                Client sess = it.next();
                if(sess.getSession().isOpen()){
                    consumer.accept(sess);
                }else{
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

    public <T extends Session> void removeAllSessions(T... sessions){
        clients.removeAll(Arrays.asList(sessions));
    }

    public <T extends Collection<? extends Session>> void removeAllSessions(T collection){
        clients.removeAll(collection);
    }
}
