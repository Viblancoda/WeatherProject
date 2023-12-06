package dacd.blanco.control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import dacd.blanco.model.Weather;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.Instant;

public class JMSWeatherStore implements WeatherSender {

    private final String brokerUrl;
    private static final String topicName = "prediction.Weather";

    public JMSWeatherStore(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    @Override
    public void sendWeather(Weather weather) {
        try (Connection connection = new ActiveMQConnectionFactory(brokerUrl).createConnection()) {
            connection.start();

            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                 MessageProducer producer = session.createProducer(session.createTopic(topicName))) {

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                                context.serialize(src.toString()))
                        .create();

                String jsonWeather = gson.toJson(weather);

                if (jsonWeather != null && !jsonWeather.equals("null")) {
                    System.out.println(weather);

                    TextMessage textMessage = session.createTextMessage(jsonWeather);
                    producer.send(textMessage);

                    System.out.println("Message send to broker: " + jsonWeather);
                } else {
                    System.out.println("Error at sending message");
                }
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
