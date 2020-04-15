import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    int portNumber = 8939;
    String hostName = "localhost";
    String nick;
    ClientConnection clientConnection;
    public static void main(String[] args) {
        //in order to get nickname
        BufferedReader obj = new BufferedReader(new InputStreamReader(System.in));
        String s = null;
        System.out.println("Tell me your nickname: ");
        try {
            s = obj.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Hello " + s + ". You can chat now!");
        new Client(s);

    }

    public Client(String nick){
        this.nick = nick;
        try {
            Socket socket = new Socket(hostName, portNumber);
            clientConnection = new ClientConnection(socket, this);
            clientConnection.start();
            listenForInput();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getNick() {
        return nick;
    }

    public void listenForInput(){
        Scanner console = new Scanner(System.in);
        while(true){
            //we want to sleep thread
            while(!console.hasNext()){
                try {
                    //is it really helpfull? Probably yes, but I am not sure!
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String input = console.nextLine();

            if(input.toLowerCase().equals("quit")){
                break;
            }

            clientConnection.sendStringToServer(this.nick + "> " + input);
        }
        clientConnection.close();
    }
}
