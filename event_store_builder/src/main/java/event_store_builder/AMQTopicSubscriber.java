package event_store_builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class AMQTopicSubscriber implements Subscriber{
    private final Connection connection;
    private final String clientID = "prediction-provider";
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

            MessageConsumer durableSubscriber = session.createDurableSubscriber(destination, "prediction-provider"+ topicName);


            durableSubscriber.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {

                        listener.consume(((TextMessage) message).getText());

                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
            Thread.sleep(Long.MAX_VALUE);

            connection.close();
        } catch (JMSException | InterruptedException e) {
            throw new RuntimeException("Error while receiving JMS message", e);
        }
    }
}