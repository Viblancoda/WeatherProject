package event_store_builder;

import javax.jms.JMSException;
import java.io.File;

public class MainReceiver {
    private static final String directory = "datalake" + File.separator + "eventstore";

    public static void main(String[] args) throws JMSException {
        Subscriber subscriber = new AMQTopicSubscriber(args[0]);
        FileEventStoreBuilder listener = new FileEventStoreBuilder(directory);
        subscriber.start(listener);
    }
}