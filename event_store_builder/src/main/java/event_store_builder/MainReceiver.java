package event_store_builder;

import javax.jms.JMSException;

public class MainReceiver {
    public static void main(String[] args) throws JMSException {
        Subscriber subscriber = new AMQTopicSubscriber(args[0]);
        Listener listener = new FileEventStoreBuilder(args[1]);
        String topicName = "prediction.Weather";
        subscriber.start(listener, topicName);
    }
}