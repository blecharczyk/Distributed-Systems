import com.rabbitmq.client.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Administration {

    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public static void main(String[] args) throws Exception {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("*ADMINISTRATION*");

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
        channel.queueBind(queueAdmin, EXCHANGE_SPACE_AGENCY, "#");
        channel.queueBind(queueAdmin, EXCHANGE_ADMIN, "#");
        channel.queueBind(queueAdmin, EXCHANGE_SHIPPER, "#");

        Consumer consumer_admin = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                handleMessage(new String(body, "UTF-8"));
                System.out.print(ANSI_RED  + "Type your message in format: <all/agency/shipper>.<message>: " + ANSI_RESET);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        channel.basicConsume(queueAdmin, false, consumer_admin);

        String s;
        while(true){
            s = "unknown";
            System.out.print(ANSI_RED  + "Type your message in format: <all/agency/shipper>.<message>: " + ANSI_RESET);
            String message[] = br.readLine().split("\\.");
            if(message.equals("exit"))
                break;
            if(message[0].equals("all"))
                s = "all";
            else if(message[0] .equals("agency"))
                s = "agency";
            else if(message[0] .equals("shipper"))
                s = "shipper";

            String adminsMessage = message[0]  + "." + s  +  ".administrator." + formatter.format(Calendar.getInstance().getTime()) + "." + message[1];
            channel.basicPublish(EXCHANGE_ADMIN, message[0], null, adminsMessage.getBytes("UTF-8"));
            System.out.println(ANSI_CYAN + "Admin " + " sent: " + adminsMessage + " with key " + message[0] + ANSI_RESET);
        }
    }

    private static void handleMessage(String s){
        String message[] = s.split("\\.");
        System.out.println(ANSI_BLUE + "\n\n\n*MESSAGE RECEIVED*");
        System.out.println("KEY : \t " + message[0]);
        System.out.println("RECEIVER : \t" + message[1]);
        System.out.println("SENDER : \t" + message[2]);
        System.out.println("DATE : \t" + message[3]);
        if(message.length > 4)
            System.out.println("MESSAGE CONTENT : \t" + message [4] + ANSI_RESET);
        System.out.println("\n\n");
    }
}
