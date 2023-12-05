package dacd.blanco.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dacd.blanco.model.Location;
import dacd.blanco.model.Weather;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.jsoup.Jsoup;

public class OpenWeatherMapProvider implements WeatherProvider {
    private static String apiKey;

    public OpenWeatherMapProvider(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Weather get(Location location, Instant instant) {
        try {
            String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=" + location.getLatitude() +
                    "&lon=" + location.getLongitude() +
                    "&appid=" + apiKey + "&units=metric";

            String jsonString = Jsoup.connect(apiUrl).ignoreContentType(true).execute().body();

            Gson gson = new Gson();
            JsonObject weatherJson = gson.fromJson(jsonString, JsonObject.class);
            JsonArray weatherJsonArray = weatherJson.getAsJsonObject().getAsJsonArray("list");

            for (JsonElement weather : weatherJsonArray) {
                JsonObject weatherJsonObject = weather.getAsJsonObject();

                double temperature = weatherJsonObject.getAsJsonObject("main").get("temp").getAsDouble();
                int humidity = weatherJsonObject.getAsJsonObject("main").get("humidity").getAsInt();
                double rainProb = weatherJsonObject.get("pop").getAsDouble();
                int clouds = weatherJsonObject.getAsJsonObject("clouds").get("all").getAsInt();
                double windSpeed = weatherJsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
                Instant predictionTs = Instant.ofEpochSecond(weatherJsonObject.get("dt").getAsLong()).truncatedTo(ChronoUnit.HOURS);

                if (predictionTs.equals(instant)) {
                    return new Weather(location, clouds, windSpeed, rainProb, temperature, humidity, predictionTs);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}