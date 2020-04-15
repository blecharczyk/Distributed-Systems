import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerConnection extends Thread {
    Socket socket;
    Server server;
    DataInputStream dataInput;
    DataOutputStream dataOutput;
    boolean shouldRun = true;


    public ServerConnection(Socket socket, Server server){
        this.socket = socket;
        this.server = server;
    }

    public void sendStringToClient(String text){
        try {
            dataOutput.writeUTF(text);
            dataOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendStringToAllClients(String text){
        for(int index = 0; index < server.connections.size(); index++){
            ServerConnection sc = server.connections.get(index);
            sc.sendStringToClient(text);
        }
    }

    public void run(){
        try {
            dataInput = new DataInputStream(socket.getInputStream());
            dataOutput = new DataOutputStream(socket.getOutputStream());

            while(shouldRun){
                while(dataInput.available() == 0){
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String textIn = dataInput.readUTF();
                sendStringToAllClients(textIn);
            }

            dataInput.close();
            dataOutput.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
