package event_store_builder;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class AMQTopicSubscriber implements Subscriber {
    private final Connection connection;
    private final String clientID = "client1";
    private final String weatherTopic = "prediction.Weather";
    private final String hotelTopic = "reservation.Hotel";
    private final Session session;

    public AMQTopicSubscriber(String brokerUrl) throws JMSException {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            connection = connectionFactory.createConnection();
            connection.setClientID(clientID);
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void start(Listener listener) {
        try {
            Topic destination = session.createTopic(weatherTopic);
            MessageConsumer consumer = session.createDurableSubscriber(destination, clientID + weatherTopic);
            consumer.setMessageListener(message -> {
                try {
                    listener.consume(((TextMessage) message).getText(), weatherTopic);
                    System.out.println(message);
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (JMSException e) {
            throw new RuntimeException("Error setting up MessageListener", e);
        }
        try {
            Topic destination = session.createTopic(hotelTopic);
            MessageConsumer consumer = session.createDurableSubscriber(destination, clientID + hotelTopic);
            consumer.setMessageListener(message -> {
                try {
                    listener.consume(((TextMessage) message).getText(), hotelTopic);
                    System.out.println(message);
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (JMSException e) {
            throw new RuntimeException("Error setting up MessageListener", e);
        }
    }
}
