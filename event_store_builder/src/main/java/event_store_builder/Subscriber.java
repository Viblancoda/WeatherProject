package event_store_builder;

import javax.jms.JMSException;
import javax.jms.Message;

public interface Subscriber {
    void start();
}
