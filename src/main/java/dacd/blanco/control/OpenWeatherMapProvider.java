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
    private static String url;

    public OpenWeatherMapProvider(String apiKey, String url) {
        this.apiKey = apiKey;
        this.url = url;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static String getUrl() {
        return url;
    }

    @Override
    public Weather get(Location location, Instant instant) {
        try {
            String apiUrl = url + "?lat=" + location.getLatitude() +
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
                Instant dt = Instant.ofEpochSecond(weatherJsonObject.get("dt").getAsLong()).truncatedTo(ChronoUnit.HOURS);

                if (dt.equals(instant)) {
                    return new Weather(location, clouds, windSpeed, rainProb, temperature, humidity, dt);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}