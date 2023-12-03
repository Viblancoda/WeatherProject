// WeatherMessageReceiver.java
package event_store_builder;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class WeatherMessageReceiver {
    private static final String BROKER_URL = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static final String TOPIC_NAME = "prediction.Weather";
    private static final String CLIENT_ID = "Blanco";
    private static final String SUBSCRIBER_NAME = "Subscriber";
    private static final String DIRECTORY_PATH = "weather_predictions";

    public void start() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            try (Connection connection = connectionFactory.createConnection()) {
                connection.setClientID(CLIENT_ID);
                connection.start();

                try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                    MessageConsumer consumer = session.createDurableSubscriber(session.createTopic(TOPIC_NAME), SUBSCRIBER_NAME);

                    System.out.println("\n");

                    WeatherMessageHandler messageHandler = new WeatherMessageHandler(DIRECTORY_PATH);

                    consumer.setMessageListener(message -> {
                        try {
                            String weatherJson = ((TextMessage) message).getText();
                            System.out.println("Message received: " + weatherJson);
                            messageHandler.storeMessage(weatherJson);
                        } catch (JMSException e) {
                            throw new RuntimeException("Error processing JMS message", e);
                        }
                    });

                    Thread.sleep(Thread.MAX_PRIORITY);
                }
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Error in the main process: " + e.getMessage());
        }
    }
}
