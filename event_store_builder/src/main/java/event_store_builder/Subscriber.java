package event_store_builder;

public interface Subscriber {
    void start(Listener listener, String topicName);
}