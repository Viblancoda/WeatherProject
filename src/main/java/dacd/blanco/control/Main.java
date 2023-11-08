package dacd.blanco.control;
import static spark.Spark.get;
import static spark.Spark.post;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dacd.blanco.model.Weather;
import org.jsoup.Jsoup;

public class Main {
    public static void main(String[] args) {
        try {
            String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=28.0997300&lon=-15.4134300&appid=c9c2b1c414da7fcb6729cb70576280f7&units=metric";
            String jsonString = Jsoup.connect(apiUrl).ignoreContentType(true).execute().body();

            Gson gson = new Gson();
            JsonObject weatherData = gson.fromJson(jsonString, JsonObject.class);

            JsonArray list = weatherData.getAsJsonArray("list");

            for (JsonElement element : list) {
                JsonObject weatherInfo = element.getAsJsonObject();
                JsonObject mainData = weatherInfo.getAsJsonObject("main");
                double temperature = mainData.get("temp").getAsDouble();
                double humidity = mainData.get("humidity").getAsDouble();
                int clouds = weatherInfo.getAsJsonObject("clouds").get("all").getAsInt();
                double windSpeed = weatherInfo.getAsJsonObject("wind").get("speed").getAsDouble();
                double rainProb = weatherInfo.get("pop").getAsDouble();

                Weather weather = new Weather(temperature, humidity, clouds, windSpeed, rainProb);

                System.out.println("Weather Info:");
                System.out.println("Temperature: " + weather.getTemperature() + ". Humidity: " + weather.getHumidity()
                + ". Clouds: " + weather.getClouds() + ". Wind Speed: " + weather.getWindSpeed() + ". Rain Probability: " + weather.getRainProb());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}