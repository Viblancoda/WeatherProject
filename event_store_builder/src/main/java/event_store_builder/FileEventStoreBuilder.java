package event_store_builder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class FileEventStoreBuilder implements Listener {
    private final String directory;
    private final Gson gson;

    public FileEventStoreBuilder() {
        this.directory = "eventstore/prediction.Weather";
        this.gson = new Gson();
    }

    @Override
    public void consume(String message){
        System.out.println("Message content: " + message);
        JsonObject jsonObject = gson.fromJson(message, JsonObject.class);

        String ss = jsonObject.get("ss").getAsString();
        String ts = jsonObject.get("ts").getAsString();

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(ts);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = zonedDateTime.format(formatter);

        String directoryPath = directory + "\\" + ss;
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
            System.out.println("Directory created");
        }

        String filePath = directoryPath + "\\" + formattedDate + ".events";
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(message + "\n");
            System.out.println("Message appended to file: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file", e);
        }
    }
}