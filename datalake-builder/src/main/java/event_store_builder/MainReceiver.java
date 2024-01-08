package event_store_builder;

import javax.jms.JMSException;
import java.io.File;

public class MainReceiver {

    public static void main(String[] args) throws JMSException {
        String root = args[1];
        String directory = root + File.separator + "datalake" + File.separator + "eventstore";
        Subscriber subscriber = new AMQTopicSubscriber(args[0]);
        FileEventStoreBuilder listener = new FileEventStoreBuilder(directory);
        subscriber.start(listener);
    }
}