package event_store_builder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class FileEventStoreBuilder implements Listener {
    private final String directory;

    public FileEventStoreBuilder(String directory) {
        this.directory = directory;
    }

    @Override
    public void consume(String message, String topic){
        System.out.println("Message received");
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(message, JsonObject.class);

        String ss = jsonObject.get("ss").getAsString();
        String ts = jsonObject.get("ts").getAsString();

        Instant instant = Instant.parse(ts);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = dateTime.format(formatter);

        String directoryPath = directory + File.separator + topic + File.separator + ss;
        createDirectory(directoryPath);

        String filePath = directoryPath + File.separator + formattedDate + ".events";
        writeMessage(filePath, message);
    }

    private void createDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
            System.out.println("Directory created");
        }
    }

    private void writeMessage(String filePath, String message) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(message + "\n");
            System.out.println("Message written: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file", e);
        }
    }
}