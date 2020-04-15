import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientConnection extends Thread{

    Socket socket;
    Client client;
    DataInputStream dataInput;
    DataOutputStream dataOutput;
    boolean shouldRun = true;

    public ClientConnection(Socket socket, Client client){
        this.socket = socket;
        this.client = client;
    }

    public void sendStringToServer(String text){
        try {
            dataOutput.writeUTF(text); //max 256 characters, add control sum
            dataOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }

    }

    public void run(){
        try {
            dataInput = new DataInputStream(socket.getInputStream());
            dataOutput = new DataOutputStream(socket.getOutputStream());

            while(shouldRun){
                try{
                    while(dataInput.available() == 0){
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    String reply = dataInput.readUTF();
                    System.out.println(reply);

                } catch (IOException e){
                    e.printStackTrace();
                    close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }

    }

    public void close(){
        try {
            dataInput.close();
            dataOutput.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
