import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    int portNumber = 8939;
    ServerSocket serverSocket;
    ArrayList<ServerConnection> connections = new ArrayList<ServerConnection>();
    boolean shouldRun = true;

    public static void main(String[] args) {
        new Server();
    }

    public Server(){
        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server works...");
            //we have to accept connections from connecting clients
            while(shouldRun){
                Socket socket = serverSocket.accept(); //this makes connection
                ServerConnection serverConnection = new ServerConnection(socket, this);
                serverConnection.start();
                connections.add(serverConnection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
