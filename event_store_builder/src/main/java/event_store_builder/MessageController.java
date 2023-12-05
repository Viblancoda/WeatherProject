package event_store_builder;

import javax.jms.Message;

public class MessageController {
    private final MessageReader messageReader;
    private final MessageSaver messageSaver;

    public MessageController(MessageReader messageReader, MessageSaver messageSaver) {
        this.messageReader = messageReader;
        this.messageSaver = messageSaver;
    }

    public void execute(Message message) {
        try {
            String weatherJson = messageReader.readMessage(message);
            System.out.println("Message received: " + weatherJson);
            messageSaver.saveMessage(weatherJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}