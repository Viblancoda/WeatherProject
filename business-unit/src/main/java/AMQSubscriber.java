import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class AMQSubscriber implements Subscriber{
    private final Connection connection;
    private final Session session;
    private static final String weatherTopic = "prediction.Weather";
    private static final String hotelTopic = "reservation.Hotel";

    public AMQSubscriber(String brokerUrl) throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @Override
    public void start(Listener listener) {
        try {
            Topic weatherDestination = session.createTopic(weatherTopic);
            Topic hotelDestination = session.createTopic(hotelTopic);

            MessageConsumer weatherConsumer = session.createConsumer(weatherDestination);
            MessageConsumer hotelConsumer = session.createConsumer(hotelDestination);

            weatherConsumer.setMessageListener(message -> handleMessage(message, listener, weatherTopic));
            hotelConsumer.setMessageListener(message -> handleMessage(message, listener, hotelTopic));
        } catch (JMSException e) {
            throw new RuntimeException("Error setting up MessageListener", e);
        }
    }

    private void handleMessage(Message message, Listener listener, String topicName) {
        try {
            listener.consume(((TextMessage) message).getText(), topicName);
            System.out.println(message);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}