package event_store_builder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class AMQTopicSubscriber implements Subscriber {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String topicName = "prediction.Weather";
    private static String clientId = "ID";
    private static String subscriptionName = "Subscriber";

    private final Listener listener;

    public AMQTopicSubscriber(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void start() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Connection connection = connectionFactory.createConnection();
            connection.setClientID(clientId);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageConsumer consumer = session.createDurableSubscriber(session.createTopic(topicName), subscriptionName);

            System.out.println("\n");

            consumer.setMessageListener(message -> {
                try {
                    String weatherJson = ((TextMessage) message).getText();
                    System.out.println("Message received: " + weatherJson);

                    String ss = parseSSFromMessage(weatherJson);
                    String dt = parseDTFromMessage(weatherJson);

                    listener.consume(weatherJson, ss, dt);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private String parseSSFromMessage(String message) {
        JsonObject jsonObject = new Gson().fromJson(message, JsonObject.class);
        return jsonObject.get("ss").getAsString();
    }

    private String parseDTFromMessage(String message) {
        JsonObject jsonObject = new Gson().fromJson(message, JsonObject.class);
        String tsValue = jsonObject.get("ts").getAsString();
        Instant instant = Instant.parse(tsValue);
        return DateTimeFormatter.ofPattern("yyyyMMdd").format(instant);
    }
}