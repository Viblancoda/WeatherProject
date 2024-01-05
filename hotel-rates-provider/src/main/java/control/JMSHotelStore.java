package control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import model.Reservation;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.Instant;

public class JMSHotelStore implements HotelSender{
    private static String brokerUrl;
    private static final String topicName = "reservation.Hotel";

    public JMSHotelStore(String brokerUrl) {
        JMSHotelStore.brokerUrl = brokerUrl;
    }

    @Override
    public void send(Reservation reservation) {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(topicName);

            MessageProducer producer = session.createProducer(destination);

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                            context.serialize(src.toString()))
                    .create();

            String json = gson.toJson(reservation);

            if (json != null && !json.equals("null")) {


                TextMessage textMessage = session.createTextMessage(json);

                producer.send(textMessage);

                System.out.println("Message send to broker: " + json);
            } else {
                System.out.println("Error at sending message");

            }
            connection.close();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}