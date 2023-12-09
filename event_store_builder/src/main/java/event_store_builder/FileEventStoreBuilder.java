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
    public void consume(String message) throws MyException.FileEventStoreException{
        System.out.println("Message content: " + message);
        JsonObject jsonObject = gson.fromJson(message, JsonObject.class);

        String ss = jsonObject.get("ss").getAsString();
        String ts = jsonObject.get("ts").getAsString();

        Instant instant = Instant.parse(ts);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = dateTime.format(dateFormat);

        String directoryPath = directory + File.separator + ss;
        File directory = new File(directoryPath);
        if (directory.mkdirs()) {
            System.out.println("Created directory");
        }

        String filePath = directoryPath + File.separator + date + ".events";
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(message + "\n");
            System.out.println("Message saved in: " + filePath);
        } catch (IOException e) {
            throw new MyException.FileEventStoreException("Error saving message", e);
        }
    }
}