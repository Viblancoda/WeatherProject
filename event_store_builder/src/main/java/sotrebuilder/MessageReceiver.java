package sotrebuilder;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class MessageReceiver {

    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject = "prediction.Weather";

    public static void main(String[] args) throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createTopic(subject);

        MessageConsumer consumer = session.createConsumer(destination);

        while (true) {
            Message message = consumer.receive();

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String weatherJson = textMessage.getText();

                EventStoreBuilder.saveEvent(weatherJson);
            }
        }
    }
}
