package control;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class AMQSubscriber implements Subscriber {
    private final Connection connection;
    private final String clientID = "DataMartClient";
    private final String[] topics = {"prediction.Weather", "reservation.Hotel"};
    private final Session session;

    public AMQSubscriber(String brokerUrl) throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connection = connectionFactory.createConnection();
        connection.setClientID(clientID);
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @Override
    public void start(Listener listener) {
        try {
            for (String topic : topics) {
                Topic destination = session.createTopic(topic);
                MessageConsumer consumer = session.createDurableSubscriber(destination, clientID + topic);
                consumer.setMessageListener(message -> {
                    try {
                        listener.consume(((TextMessage) message).getText(), topic);
                        System.out.println(message);
                    } catch (JMSException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (JMSException e) {
            throw new RuntimeException("Error setting up MessageListener", e);
        }
    }
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}