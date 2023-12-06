package event_store_builder;

public interface Listener {
    void consume(String messageContent);
}
