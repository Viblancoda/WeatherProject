package control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import model.Reservation;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.time.Instant;

public class JMSHotelStore implements HotelSender {

    private final String brokerUrl;
    private static final String topicName = "prediction.Weather";

    public JMSHotelStore(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    @Override
    public void send(Reservation reservation) {
        try (Connection connection = new ActiveMQConnectionFactory(brokerUrl).createConnection()) {
            connection.start();

            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                 MessageProducer producer = session.createProducer(session.createTopic(topicName))) {

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                                context.serialize(src.toString()))
                        .create();

                String jsonHotel = gson.toJson(reservation);

                if (jsonHotel != null && !jsonHotel.equals("null")) {
                    System.out.println(reservation);

                    TextMessage textMessage = session.createTextMessage(jsonHotel);
                    producer.send(textMessage);

                    System.out.println("Message send to broker: " + jsonHotel);
                } else {
                    System.out.println("Error at sending message");
                }
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}