package event_store_builder;

public interface MessageSaver {
    void saveMessage(String messageContent);
}
