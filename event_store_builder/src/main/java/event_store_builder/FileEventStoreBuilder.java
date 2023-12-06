package event_store_builder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileEventStoreBuilder implements Listener {
    private String baseDirectory;

    public FileEventStoreBuilder(String baseDirectory) {
        this.baseDirectory = baseDirectory;
        createDirectoryIfNotExists();
    }

    private void createDirectoryIfNotExists() {
        try {
            if (!Files.exists(Path.of(baseDirectory))) {
                Files.createDirectories(Path.of(baseDirectory));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error creating base directory: " + e.getMessage());
        }
    }

    @Override
    public void consume(String messageContent, String ss, String dt) {
        try {
            String subDirectory = baseDirectory + "/prediction.Weather/" + ss;

            createDirectoryIfNotExists(subDirectory);

            String currentExecutionFileName = subDirectory + "/" + dt + ".events";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentExecutionFileName, true))) {
                writer.write(messageContent + "\n");
                System.out.println("Message saved to: " + currentExecutionFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving message to file: " + e.getMessage());
        }
    }

    private void createDirectoryIfNotExists(String directoryPath) {
        try {
            if (!Files.exists(Path.of(directoryPath))) {
                Files.createDirectories(Path.of(directoryPath));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error creating directory: " + e.getMessage());
        }
    }
}