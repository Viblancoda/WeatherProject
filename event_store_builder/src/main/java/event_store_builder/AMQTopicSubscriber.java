package event_store_builder;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class AMQTopicSubscriber implements Subscriber{
    private final Connection connection;
    private final String clientID = "clientID";
    private final Session session;

    public AMQTopicSubscriber(String url) throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connection = connectionFactory.createConnection();
        connection.setClientID(clientID);
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @Override
    public void start(Listener listener, String topicName) {
        try {
            Topic destination = session.createTopic(topicName);
            MessageConsumer durableSubscriber = session.createDurableSubscriber(destination, "prediction-provider-" + topicName);

            durableSubscriber.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                        String messageText = ((TextMessage) message).getText();
                        listener.consume(messageText);
                    } catch (JMSException e) {
                        throw new RuntimeException("Error while consuming JMS message", e);
                    }
                }
            });
        } catch (JMSException e) {
            throw new RuntimeException("Error while receiving JMS message", e);
        }
    }
}