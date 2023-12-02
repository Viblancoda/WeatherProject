package dacd.blanco.control;

import javax.jms.*;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.time.Instant;
import dacd.blanco.model.Weather;
import com.google.gson.Gson;

public class JMSWeatherStore implements WeatherStore {

    private static final String BROKER_URL = ActiveMQConnectionFactory.DEFAULT_BROKER_URL;
    private static final String QUEUE_NAME = "WEATHER_QUEUE";

    @Override
    public void saveWeather(Weather weather) {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createQueue(QUEUE_NAME);

            MessageProducer producer = session.createProducer(destination);

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                            context.serialize(src.getEpochSecond()))
                    .create();

            String weatherJson = gson.toJson(weather);
            TextMessage weatherMessage = session.createTextMessage(weatherJson);

            producer.send(weatherMessage);

            System.out.println("Weather sent to JMS broker.");

            connection.close();
        } catch (JMSException e) {
            throw new RuntimeException("Error storing weather data to JMS", e);
        }
    }
}
