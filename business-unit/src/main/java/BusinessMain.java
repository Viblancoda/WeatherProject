import javax.jms.JMSException;
import java.io.File;

public class BusinessMain {
    private static final String directory = "datamart" + File.separator + "eventstore";

    public static void main(String[] args) throws JMSException {
        Subscriber subscriber = new AMQSubscriber(args[0]);
        Listener listener = new DataMartBuilder(directory);
        subscriber.start(listener);
    }
}
