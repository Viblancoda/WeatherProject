package sotrebuilder;

import dacd.blanco.model.Weather;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventStoreBuilder {

    public static void saveEvent(String data) {
        try {
            long timestamp = extractTimestamp(data);

            String directoryPath = createDirectoryStructure(timestamp);

            saveToFile(directoryPath, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long extractTimestamp(String data) {
        return System.currentTimeMillis();
    }

    private static String createDirectoryStructure(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(timestamp);
        String dateStr = dateFormat.format(date);

        String directoryPath = "eventstore/prediction.Weather/" + Weather.getSs() + "/" + dateStr + ".events";

        new File(directoryPath).mkdirs();

        return directoryPath;
    }

    private static void saveToFile(String directoryPath, String data) throws IOException {
        String filePath = directoryPath + "/" + System.currentTimeMillis() + ".json";
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(data);
        }
    }
}
