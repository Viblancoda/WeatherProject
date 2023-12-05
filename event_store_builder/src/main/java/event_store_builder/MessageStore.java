package event_store_builder;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import dacd.blanco.model.Weather;

public class MessageStore implements MessageSaver {
    private String baseDirectory;
    private String currentExecutionFileName;

    public MessageStore(String baseDirectory) {
        this.baseDirectory = baseDirectory;
        createDirectoryIfNotExists();
        initializeExecutionFile();
    }

    private void createDirectoryIfNotExists() {
        try {
            if (!Files.exists(Path.of(baseDirectory))) {
                Files.createDirectory(Path.of(baseDirectory));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error creating base directory: " + e.getMessage());
        }
    }

    private void initializeExecutionFile() {
        SimpleDateFormat executionDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date predictionDt = new Date(Weather.getPredictionDt().toEpochMilli());
        currentExecutionFileName = baseDirectory + "/" + executionDateFormat.format(predictionDt) + "/events.log";
    }

    @Override
    public void saveMessage(String messageContent) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentExecutionFileName, true))) {
            writer.write(messageContent + "\n");
            System.out.println("Message saved to: " + currentExecutionFileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving message to file: " + e.getMessage());
        }
    }
}