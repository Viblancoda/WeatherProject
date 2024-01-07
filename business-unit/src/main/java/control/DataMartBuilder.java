package control;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DataMartBuilder implements Listener {
    private final String directory;
    private final String file = "all-events";

    public DataMartBuilder(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }

    public List<String> readEvents() {
        List<String> events = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(directory + File.separator + file + ".events"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                events.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return events;
    }

    public void consume(String message, String topicName) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(message, JsonObject.class);

        String tsValue = jsonObject.get("ts").getAsString();

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(tsValue);

        if (zonedDateTime.toLocalDate().equals(LocalDate.now())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String formattedDate = zonedDateTime.format(formatter);

            String directoryPath = directory + File.separator + formattedDate;
            createDirectory(directoryPath);

            cleanOldEvents(directory, formattedDate);

            String filePath = directoryPath + File.separator + file + ".events";
            writeMessageToFile(filePath, message);
        }
    }

    private void createDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
            System.out.println("Directory created");
        }
    }

    private void writeMessageToFile(String filePath, String message) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(message + "\n");
            System.out.println("Message written: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file", e);
        }
    }

    private void cleanOldEvents(String basePath, String currentFolder) {
        File baseDirectory = new File(basePath);
        File[] subdirectories = baseDirectory.listFiles(File::isDirectory);

        if (subdirectories != null) {
            for (File subdirectory : subdirectories) {
                if (!subdirectory.getName().equals(currentFolder)) {
                    deleteDirectory(subdirectory);
                }
            }
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