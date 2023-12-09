package event_store_builder;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class AMQTopicSubscriber implements Subscriber{
    private final Connection connection;
    private final String clientID = "clientID";
    private final Session session;

    public AMQTopicSubscriber(String url) throws MyException.JMSConnectionException {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            connection = connectionFactory.createConnection();
            connection.setClientID(clientID);
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }
        catch(JMSException e){
            throw new MyException.JMSConnectionException("Error while creating JMS connection", e);
        }
    }

    @Override
    public void start(Listener listener, String topicName) throws MyException.JMSMessageReceivingException {
        try {
            Topic destination = session.createTopic(topicName);
            String subscriberName = "prediction-provider-" + topicName;
            MessageConsumer durableSubscriber = session.createDurableSubscriber(destination, subscriberName);

            durableSubscriber.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                        processTextMessage((TextMessage) message, listener);
                    } catch (MyException.JMSMessageProcessingException | MyException.FileEventStoreException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (JMSException e) {
            throw new MyException.JMSMessageReceivingException("Error while receiving JMS message", e);
        }
    }

    private void processTextMessage(TextMessage textMessage, Listener listener) throws MyException.JMSMessageProcessingException, MyException.FileEventStoreException {
        try {
            String messageText = textMessage.getText();
            listener.consume(messageText);
        } catch (JMSException e) {
            throw new MyException.JMSMessageProcessingException("Error while consuming JMS message", e);
        }
    }
}