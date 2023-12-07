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
    private final Gson gson;

    public FileEventStoreBuilder(String directory) {
        this.directory = directory;
        this.gson = new Gson();
    }

    @Override
    public void consume(String message) {
        System.out.println("message: " + message);
        JsonObject jsonObject = gson.fromJson(message, JsonObject.class);

        String ssValue = jsonObject.get("ss").getAsString();
        String tsValue = jsonObject.get("ts").getAsString();

        Instant instant = Instant.parse(tsValue);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = dateTime.format(formatter);

        String directoryPath = directory + File.separator + ssValue;
        File directory = new File(directoryPath);
        if (directory.mkdirs()) {
            System.out.println("Directory created");
        }

        String filePath = directoryPath + File.separator + formattedDate + ".events";
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(message + "\n");
            System.out.println("Message saved in: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file", e);
        }
    }
}