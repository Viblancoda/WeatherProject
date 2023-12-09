package event_store_builder;

public class MainReceiver {
    public static void main(String[] args) throws CustomJMSException {
        Subscriber subscriber = new AMQTopicSubscriber(args[0]);
        Listener listener = new FileEventStoreBuilder(args[1]);
        String topicName = "prediction.Weather";
        subscriber.start(listener, topicName);
    }
}