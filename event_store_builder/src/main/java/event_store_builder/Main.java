package event_store_builder;

public class Main {
    public static void main(String[] args) {
        String baseDirectory = "eventstore";

        Listener fileEventStoreBuilder = new FileEventStoreBuilder(baseDirectory);
        Subscriber amqTopicSubscriber = new AMQTopicSubscriber(fileEventStoreBuilder);

        amqTopicSubscriber.start();
    }
}
