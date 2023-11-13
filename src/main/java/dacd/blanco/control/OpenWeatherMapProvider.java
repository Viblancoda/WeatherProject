package dacd.blanco.control;
import java.net.HttpURLConnection;
import java.net.URL;
import dacd.blanco.model.Location;
import dacd.blanco.model.Weather;
import java.time.Instant;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import com.google.gson.Gson;

public class OpenWeatherMapProvider implements WeatherProvider{
    private String apiKey;
    private String templateUrl;

    public OpenWeatherMapProvider(String apiKey, String templateUrl) {
        this.apiKey = apiKey;
        this.templateUrl = templateUrl;
    }
    @Override
    public Weather get(Location location, Instant instant) {
        String apiUrl = buildApiUrl(location, instant);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            Weather weather = parseJsonResponse(response.toString());

            return weather;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String buildApiUrl(Location location, Instant instant) {
        return String.format("https://api.openweathermap.org/data/2.5/forecast?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() +
                "&appid=c9c2b1c414da7fcb6729cb70576280f7&units=metric");
    }

    private Weather parseJsonResponse(String jsonResponse) {
        Gson gson = new Gson();
        Weather weather = gson.fromJson(jsonResponse, Weather.class);

        return weather;
    }
}