package event_store_builder;
import dacd.blanco.model.Weather;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JMRWeatherStore implements MessageReader {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String topicName = "prediction.Weather";
    private static String clientId = "ID";
    private static String subscriptionName = "Subscriber";

    @Override
    public String readMessage(Message message) throws JMSException {
        return ((TextMessage) message).getText();
    }

    public void startListening(MessageController messageController) {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Connection connection = connectionFactory.createConnection();
            connection.setClientID(clientId);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageConsumer consumer = session.createDurableSubscriber(session.createTopic(topicName), subscriptionName);

            System.out.println("\n");

            consumer.setMessageListener(message -> messageController.execute(message));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}