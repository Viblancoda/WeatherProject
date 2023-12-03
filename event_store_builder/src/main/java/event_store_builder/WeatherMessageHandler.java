package event_store_builder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WeatherMessageHandler {
    private final String directoryPath;

    public WeatherMessageHandler(String directoryPath) {
        this.directoryPath = directoryPath;
        createDirectory();
    }

    public void storeMessage(String weatherJson) {
        try {
            saveMessage("weather_message" + System.currentTimeMillis() + ".json", weatherJson);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving message to file: " + e.getMessage());
        }
    }

    private void createDirectory() {
        try {
            if (!Files.exists(Path.of(directoryPath))) {
                Files.createDirectory(Path.of(directoryPath));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error creating directory: " + e.getMessage());
        }
    }

    private void saveMessage(String fileName, String messageContent) throws IOException {
        String filePath = directoryPath + "/" + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(messageContent);
            System.out.println("Message saved to: " + filePath);
        }
    }
}
