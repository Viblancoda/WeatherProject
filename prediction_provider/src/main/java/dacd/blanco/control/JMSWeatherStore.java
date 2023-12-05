package dacd.blanco.control;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import dacd.blanco.model.Weather;

public class JMSWeatherStore implements WeatherStore {

    private static final String brokerUrl = ActiveMQConnectionFactory.DEFAULT_BROKER_URL;
    private static final String topicName = "prediction.Weather";

    @Override
    public void saveWeather(Weather weather) {
        Connection connection = null;
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createTopic(topicName);

            MessageProducer producer = session.createProducer(destination);

            String weatherString = buildWeatherString(weather);

            TextMessage weatherMessage = session.createTextMessage(weatherString);

            producer.send(weatherMessage);

            System.out.println("Weather sent to JMS broker." + weatherString);
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

    private String buildWeatherString(Weather weather) {
        return String.format(
                "Location: %s, Clouds: %d, Wind Speed: %.2f, Rain Probability: %.2f, Temperature: %.2f, Humidity: %d, " +
                        "Date and Time: %s, ss: %s, Prediction Date and Time: %s",
                weather.getLocation().getName(),
                weather.getClouds(),
                weather.getWindSpeed(),
                weather.getRainProb(),
                weather.getTemperature(),
                weather.getHumidity(),
                weather.getPredictionTs(),
                Weather.getSs(),
                Weather.getTs()
        );
    }
}