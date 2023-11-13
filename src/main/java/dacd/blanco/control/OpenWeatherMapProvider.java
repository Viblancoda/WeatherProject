package dacd.blanco.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dacd.blanco.model.Location;
import dacd.blanco.model.Weather;
import com.google.gson.Gson;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.time.Instant;

public class OpenWeatherMapProvider implements WeatherProvider {
    @Override
    public Weather get(Location location, Instant instant) {
        Weather weatherObj = null;
        try {
            String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=" + location.getLatitude() +
                    "&lon=" + location.getLongitude() +
                    "&appid=c9c2b1c414da7fcb6729cb70576280f7&units=metric";

            String jsonString = Jsoup.connect(apiUrl).ignoreContentType(true).execute().body();

            Gson gson = new Gson();
            JsonObject weatherJson = gson.fromJson(jsonString, JsonObject.class);
            JsonArray weatherJsonArray = weatherJson.getAsJsonObject().getAsJsonArray("list");

            for (JsonElement weather : weatherJsonArray) {
                JsonObject weatherJsonObject = weather.getAsJsonObject();

                JsonObject main = weatherJsonObject.getAsJsonObject("main");

                double temperature = main.get("temp").getAsDouble();
                int humidity = main.get("humidity").getAsInt();
                double rainProb = weatherJsonObject.get("pop").getAsDouble();
                int clouds = weatherJsonObject.getAsJsonObject("clouds").get("all").getAsInt();
                double windSpeed = weatherJsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
                long dT = weatherJsonObject.get("dt").getAsLong();
                Instant dt = Instant.ofEpochSecond(dT);
                System.out.println("Temperature: " + temperature);
                System.out.println("Humidity: " + humidity);
                System.out.println("Rain Probability: " + rainProb);
                System.out.println("Clouds: " + clouds);
                System.out.println("Wind Speed: " + windSpeed);
                System.out.println("Timestamp: " + dt);
                if (dt.equals(instant)) {
                    weatherObj = new Weather(location, clouds, windSpeed, rainProb, temperature, humidity, dt);
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return weatherObj;
    }
}