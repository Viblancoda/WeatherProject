package event_store_builder;

import javax.jms.JMSException;

public class MainReceiver {
    private static final String topicName = "prediction.Weather";
    public static void main(String[] args) throws JMSException {
        Subscriber subscriber = new AMQTopicSubscriber(args[0]);
        Listener listener = new FileEventStoreBuilder();
        subscriber.start(listener, topicName);
    }
}