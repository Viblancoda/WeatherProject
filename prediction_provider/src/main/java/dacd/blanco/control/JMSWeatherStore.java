package dacd.blanco.control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import dacd.blanco.model.Weather;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class JMSWeatherStore implements WeatherSender {

    private static final String brokerUrl = ActiveMQConnectionFactory.DEFAULT_BROKER_URL;
    private static final String topicName = "prediction.Weather";
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_INSTANT;

    @Override
    public void sendWeather(Weather weather) {
        Connection connection = null;
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createTopic(topicName);

            MessageProducer producer = session.createProducer(destination);

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                            context.serialize(src.getEpochSecond()))
                    .create();

            JsonObject weatherJsonObject = new JsonObject();
            weatherJsonObject.addProperty("location", weather.getLocation().getName());
            weatherJsonObject.addProperty("latitude", String.valueOf(weather.getLocation().getLatitude()));
            weatherJsonObject.addProperty("longitude", String.valueOf(weather.getLocation().getLongitude()));
            weatherJsonObject.addProperty("clouds", String.valueOf(weather.getClouds()));
            weatherJsonObject.addProperty("windSpeed", String.valueOf(weather.getWindSpeed()));
            weatherJsonObject.addProperty("rainProb", String.valueOf(weather.getRainProb()));
            weatherJsonObject.addProperty("temperature", String.valueOf(weather.getTemperature()));
            weatherJsonObject.addProperty("humidity", String.valueOf(weather.getHumidity()));
            weatherJsonObject.addProperty("dt", dateFormatter.format(weather.getPredictionTs()));
            weatherJsonObject.addProperty("ss", Weather.getSs());
            weatherJsonObject.addProperty("predictionDt", dateFormatter.format(Weather.getTs()));

            String weatherJson = gson.toJson(weatherJsonObject);
            TextMessage weatherMessage = session.createTextMessage(weatherJson);

            producer.send(weatherMessage);

            System.out.println("Weather sent to JMS broker." + weatherJson);
        } catch (JMSException e) {
            throw new RuntimeException("Error storing weather data to JMS", e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}