//Yizhou Wang
//669026
//DS project1


import java.util.ArrayList;
import java.util.List;

//Singleton object that manages the server state
public class ServerState {

    private static ServerState instance;
    private List<ClientConnection> connectedClients;
    private static ArrayList<String> userList;
    private List<ClientConnection> connectedPlayers;

    private ServerState() {
        connectedClients = new ArrayList<>();
        userList = new ArrayList<String>();
    }

    public static synchronized ServerState getInstance() {
        if(instance == null) {
            instance = new ServerState();
        }
        return instance;
    }

    public synchronized void clientConnected(ClientConnection client) {
        connectedClients.add(client);
    }

    public synchronized void clientDisconnected(ClientConnection client) {
        connectedClients.remove(client);
    }

    public synchronized List<ClientConnection> getConnectedClients() {
        return connectedClients;
    }
    public synchronized void playerConnected(ClientConnection client) {
	connectedPlayers.add(client);
    }

    public synchronized void playerDisconnected(ClientConnection client) {
	connectedPlayers.remove(client);
    }

    public synchronized List<ClientConnection> getConnectedPlayers() {
        return connectedPlayers;
    }
    
    public synchronized void addUser(String username) {
        userList.add(username);
    }
    
    public synchronized void removeUser(String username) {
        userList.remove(username);
    }
    
    public synchronized ArrayList<String> getUserList() {
        return userList;
    }
}
