package event_store_builder;

import dacd.blanco.model.Weather;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SaveEvents {

    private String directoryPath;

    public SaveEvents(String directoryPath) {
        this.directoryPath = directoryPath;
        createDirectoryIfNotExists();
    }

    public void saveWeather(Weather weather) {
        try {
            String fileName = "weather_" + System.currentTimeMillis() + ".json";
            String filePath = directoryPath + "/" + fileName;

            saveToFile(filePath, weatherToJson(weather));

            System.out.println("Weather saved to: " + filePath);
        } catch (IOException e) {
            handleException("Error saving weather data", e);
        }
    }

    private String weatherToJson(Weather weather) {
        return String.format(
                "{\"location\": \"%s\",\"temperature\": %f,\"humidity\": %d,\"windSpeed\": %f,\"rainProbability\": %f,\"clouds\": %d,\"dt\": %d}",
                weather.getLocation().getName(),
                weather.getTemperature(),
                weather.getHumidity(),
                weather.getWindSpeed(),
                weather.getRainProb(),
                weather.getClouds(),
                weather.getDt().getEpochSecond()
        );
    }

    private void createDirectoryIfNotExists() {
        try {
            Files.createDirectories(Path.of(directoryPath));
        } catch (IOException e) {
            handleException("Error creating directory", e);
        }
    }

    private void saveToFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
    }

    private void handleException(String message, Exception e) {
        e.printStackTrace();
        System.err.println(message + ": " + e.getMessage());
    }
}
