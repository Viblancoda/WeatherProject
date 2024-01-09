package control;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Files;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class DataMartBuilder implements Listener {
    private final String directory;
    private final String file = "today";
    private static boolean datamartCleared = false;

    public DataMartBuilder(String directory) {
        this.directory = directory;
    }

    @Override
    public void consume(String message, String topicName) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(message, JsonObject.class);

        String timestampValue = jsonObject.get("ts").getAsString();

        ZonedDateTime eventDateTime = ZonedDateTime.parse(timestampValue);

        String formattedDate = eventDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        if (!datamartCleared) {
            clearDataMart();
            datamartCleared = true;
        }

        String directoryPath = directory + File.separator + formattedDate;
        createDirectoryIfNotExists(directoryPath);

        String filePath = directoryPath + File.separator + file + ".events";
        writeMessageToFile(filePath, message);
    }

    private void clearDataMart() {
        File baseDirectory = new File(directory);
        File[] subdirectories = baseDirectory.listFiles(File::isDirectory);

        if (subdirectories != null) {
            for (File subdirectory : subdirectories) {
                deleteDirectory(subdirectory);
            }
        }
    }

    private void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() && directory.mkdirs()) {
            System.out.println("Directory created");
        }
    }

    private void writeMessageToFile(String filePath, String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.println(message);
            System.out.println("Message written to: " + filePath);
        } catch (IOException e) {
            throw new UncheckedIOException("Error writing to file", e);
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}