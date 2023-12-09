package event_store_builder;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class AMQTopicSubscriber implements Subscriber {
    private final Connection connection;
    private final String clientID = "clientID";
    private final Session session;

    public AMQTopicSubscriber(String url) throws CustomJMSException.JMSConnectionException{
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            connection = connectionFactory.createConnection();
            connection.setClientID(clientID);
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            throw new CustomJMSException.JMSConnectionException("Error while creating JMS connection", e);
        }
    }


    @Override
    public void start(Listener listener, String topicName) throws CustomJMSException.JMSMessageReceivingException{
        try {
            Topic destination = session.createTopic(topicName);
            String subscriberName = "prediction-provider-" + topicName;
            MessageConsumer durableSubscriber = session.createDurableSubscriber(destination, subscriberName);

            durableSubscriber.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                        processTextMessage((TextMessage) message, listener);
                    } catch (CustomJMSException.JMSMessageProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            Thread.sleep(Long.MAX_VALUE);
            connection.close();
        } catch (JMSException | InterruptedException e) {
            assert e instanceof JMSException;
            throw new CustomJMSException.JMSMessageReceivingException("Error while receiving JMS message", (JMSException) e);
        }
    }

    private void processTextMessage(TextMessage textMessage, Listener listener) throws CustomJMSException.JMSMessageProcessingException{
        try {
            String messageText = textMessage.getText();
            listener.consume(messageText);
        } catch (JMSException | CustomJMSException.FileEventStoreException e) {
            throw new CustomJMSException.JMSMessageProcessingException("Error while consuming JMS message", e);

        }
    }
}
