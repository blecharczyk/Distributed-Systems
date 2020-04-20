import com.rabbitmq.client.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SpaceAgency {

    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public static void main(String[] args) throws Exception {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("*SPACE AGENCY CONFIGURATION* \nType ID: ");
        String id = br.readLine();

        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();


        String EXCHANGE_SPACE_AGENCY = "exchange_space_agnecy";
        String EXCHANGE_SHIPPER = "exchange_shipper";
        String EXCHANGE_ADMIN = "echange_admin";

        channel.exchangeDeclare(EXCHANGE_SPACE_AGENCY, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(EXCHANGE_SHIPPER, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(EXCHANGE_ADMIN, BuiltinExchangeType.TOPIC);

        String queueAdmin = channel.queueDeclare().getQueue();
        channel.queueBind(queueAdmin, EXCHANGE_ADMIN, "all.#");//???????
        channel.queueBind(queueAdmin, EXCHANGE_ADMIN, "agency.#");//???????
        channel.queueBind(queueAdmin, EXCHANGE_SHIPPER, "ack." + id + ".#");//???????

        Consumer consumer_admin = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                handleAdminMessage(new String(body, "UTF-8"));
                System.out.print(ANSI_RED + "Type your message in format: <key>.<message>: " + ANSI_RESET);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        channel.basicConsume(queueAdmin, false, consumer_admin);

        while(true){
            System.out.print(ANSI_RED + "Type your message in format: <key>.<message>: " + ANSI_RESET);
            String message[] = br.readLine().split("\\.");
            if(message.equals("exit"))
                break;
            String adminsMessage = message[0] + ".unknown." + id + "." + formatter.format(Calendar.getInstance().getTime()) + "." + message[1];
            channel.basicPublish(EXCHANGE_SPACE_AGENCY, message[0], null, adminsMessage.getBytes("UTF-8"));
            System.out.println(ANSI_CYAN + "Shipper " + id + " sent: " + adminsMessage + " with key " + message[0] + ANSI_RESET);
        }
    }

    private static void handleAdminMessage(String s){
        String message[] = s.split("\\.");
        if(message[0].equals("ack"))
            System.out.println(ANSI_BLUE + "\n\n\n*Received ACK*" + ANSI_RESET);
        else
            System.out.println(ANSI_BLUE + "\n\n\n*Received message form ADMIN*" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "KEY : \t " + message[0]);
        System.out.println("RECEIVER : \t" + message[1]);
        System.out.println("SENDER : \t" + message[2]);
        System.out.println("DATE : \t" + message[3]);
        if(message.length > 4)
            System.out.println("MESSAGE CONTENT : \t" + message [4] + ANSI_RESET);
        System.out.println("\n\n");
    }
}