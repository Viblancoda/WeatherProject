package event_store_builder;

import com.google.gson.*;
import dacd.blanco.model.Weather;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.Instant;

public class JMRWeatherStore {

    private final String url;
    private final String topicName;
    private final String clientId;
    private static final String directoryPath = "messages_directory";

    public JMRWeatherStore(String url, String topicName, String clientId, String directoryPath) {
        this.url = url;
        this.topicName = topicName;
        this.clientId = clientId;
    }

    public void receiveMessage() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Connection connection = connectionFactory.createConnection();
            connection.setClientID(clientId);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic destination = session.createTopic(topicName);

            MessageConsumer consumer = session.createDurableSubscriber(destination, "DurableSubscriber");
            consumer.setMessageListener(new WeatherMessageListener());

            Thread.sleep(Long.MAX_VALUE);

            connection.close();
        } catch (JMSException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static class WeatherMessageListener implements MessageListener {
        @Override
        public void onMessage(Message message) {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    String json = textMessage.getText();
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Instant.class, (JsonDeserializer<Instant>) (jsonElement, type, jsonDeserializationContext) ->
                                    Instant.ofEpochSecond(jsonElement.getAsJsonPrimitive().getAsLong()))
                            .create();

                    Weather weather = gson.fromJson(json, Weather.class);

                    System.out.println("Received Weather: " + json);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        String url = "tcp://localhost:61616";
        String topicName = "prediction.Weather";
        String clientId = "exampleClientId";

        JMRWeatherStore weatherStore = new JMRWeatherStore(url, topicName, clientId, directoryPath);
        weatherStore.receiveMessage();
    }
}
