import com.rabbitmq.client.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Shipper {
    static String serviceTypes[] = {"people", "cargo", "satellite"};
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    public static void main(String[] args) throws Exception{
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.print("*SHIPPER CONFIGURATION* \nType ID: ");
        String id = br.readLine();

        System.out.println("\nType shipper types: \nPress 0 for People \nPress 1 for Cargo \nPress 2 for Satellite");
        int shipping_types[] = getShippingTypes();
        System.out.println("Your choice: " + shipping_types[0] + "   " + shipping_types[1]);


        String []routingKeys = {serviceTypes[shipping_types[0]] + ".#", serviceTypes[shipping_types[1]] + ".#"};

        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        String EXCHANGE_SPACE_AGENCY = "exchange_space_agnecy";
        String EXCHANGE_SHIPPER = "exchange_shipper";
        String EXCHANGE_ADMIN = "echange_admin";

        channel.exchangeDeclare(EXCHANGE_SPACE_AGENCY, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(EXCHANGE_SHIPPER, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(EXCHANGE_ADMIN, BuiltinExchangeType.TOPIC);

        Consumer consumer_agency = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String agent_id = handleAgencyMessage(new String(body, "UTF-8"));
                channel.basicAck(envelope.getDeliveryTag(), false);
                String ack = "ack." + agent_id + "." + id + "." + formatter.format(Calendar.getInstance().getTime()) + ".Order is completed!";
                channel.basicPublish(EXCHANGE_SHIPPER, "ack." + agent_id, null, ack.getBytes("UTF-8"));
            }
        };

        for (String routingKey: routingKeys) {
            String queueNameAgency = routingKey;
            channel.queueDeclare(queueNameAgency, false, false, false, null);
            channel.queueBind(queueNameAgency, EXCHANGE_SPACE_AGENCY, routingKey);
            System.out.println(ANSI_PURPLE + "created queue: " + queueNameAgency + " routing key: " + routingKey + ANSI_RESET);
            channel.basicConsume(queueNameAgency, false, consumer_agency);
        }

        String queueNameAdmin = channel.queueDeclare().getQueue();
        channel.queueBind(queueNameAdmin, EXCHANGE_ADMIN, "all.#");
        channel.queueBind(queueNameAdmin, EXCHANGE_ADMIN, "shipper.#");

        channel.basicQos(1);

        Consumer consumer_admin = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                handleAdminMessage(new String(body, "UTF-8"));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        System.out.println(ANSI_PURPLE + "\nWaiting for messages.." + ANSI_RESET);
        channel.basicConsume(queueNameAdmin, false, consumer_admin);

    }

    private static void handleAdminMessage(String s){
        String message[] = s.split("\\.");
        System.out.println(ANSI_BLUE + "\n*ADMIN MESSAGE RECEIVED*");
        System.out.println("KEY : \t " + message[0]);
        System.out.println("RECEIVER : \t" + message[1]);
        System.out.println("SENDER : \t" + message[2]);
        System.out.println("DATE : \t" + message[3]);
        if(message.length > 4)
            System.out.println("MESSAGE CONTENT : \t" + message [4] + ANSI_RESET);
        System.out.println(ANSI_PURPLE + "\nWaiting for next messages.." + ANSI_RESET);
        System.out.println("\n");
    }

    private static String handleAgencyMessage(String s){
        String message[] = s.split("\\.");
        System.out.println(ANSI_BLUE + "\n*MESSAGE ORDER*");
        System.out.println("KEY : \t " + message[0]);
        System.out.println("RECEIVER : \t" + message[1]);
        System.out.println("SENDER : \t" + message[2]);
        System.out.println("DATE : \t" + message[3]);
        if(message.length > 4) System.out.println("MESSAGE CONTENT : \t" + message [4] + ANSI_RESET);
        System.out.println(ANSI_GREEN + "\nThe order is being processed!" + ANSI_RESET);

        try{
            for(int i=0; i<5; i++){
                TimeUnit.SECONDS.sleep(1);
                System.out.println(ANSI_GREEN + "." + ANSI_RESET);
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println(ANSI_GREEN + "*Order is completed and the ack was sent!*.\n" + ANSI_RESET);
        System.out.println(ANSI_PURPLE + "Waiting for next messages.." + ANSI_RESET);

        return message[2];
    }

    private static int[] getShippingTypes() throws Exception{
        int int_types[] = new int[2];
        while(true){
            String types[] = br.readLine().split(" ");
            boolean flag = true;

            if ("0".equals(types[0]))
                int_types[0] = 0;
            else if ("1".equals(types[0]))
                int_types[0] = 1;
            else if ("2".equals(types[0]))
                int_types[0] = 2;
            else {
                System.out.println(ANSI_RED + "Wrong arguments! Try once again!" + ANSI_RESET);
                continue;
            }

            if ("0".equals(types[1]))
                int_types[1] = 0;
            else if ("1".equals(types[1]))
                int_types[1] = 1;
            else if ("2".equals(types[1]))
                int_types[1] = 2;
            else {
                System.out.println(ANSI_RED + "Wrong arguments! Try once again!" + ANSI_RESET);
                continue;
            }

            if(int_types[0] != int_types[1])
                break;

            System.out.println(ANSI_RED + "Wrong arguments! Try once again!" + ANSI_RESET);
        }
        return int_types;
    }
}
