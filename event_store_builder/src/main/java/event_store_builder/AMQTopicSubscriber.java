package event_store_builder;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class AMQTopicSubscriber implements Subscriber {
    private final Connection connection;
    private final String clientID = "event_store_builder";
    private final Session session;

    public AMQTopicSubscriber(String url) throws JMSException {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            connection = connectionFactory.createConnection();
            connection.setClientID(clientID);
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void start(Listener listener, String topicName) {
        try {
            Topic destination = session.createTopic(topicName);
            MessageConsumer consumer = session.createDurableSubscriber(destination, clientID + topicName);
            consumer.setMessageListener(message -> {
                try {
                    listener.consume(((TextMessage) message).getText());
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
