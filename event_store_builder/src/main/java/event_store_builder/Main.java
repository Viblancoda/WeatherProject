package event_store_builder;

public class Main {
    public static void main(String[] args) {
        String baseDirectory = "eventstore";
        MessageSaver messageSaver = new MessageStore(baseDirectory);

        MessageReader messageReader = new JMRWeatherStore();

        MessageController messageController = new MessageController(messageReader, messageSaver);

        JMRWeatherStore jmrWeatherStore = new JMRWeatherStore();
        jmrWeatherStore.startListening(messageController);
    }
}