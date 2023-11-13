package dacd.blanco.control;
import dacd.blanco.model.Location;
import dacd.blanco.model.Weather;

import java.util.ArrayList;
import java.time.Instant;

public class WeatherController {
    public void displayAndSaveWeatherInfo(ArrayList<Location> islands) {
        WeatherProvider weatherProvider = new OpenWeatherMapProvider("c9c2b1c414da7fcb6729cb70576280f7", "https://api.openweathermap.org/data/2.5/forecast?lat=%.2f&lon=%.2f&appid=%s&units=metric");

        for (Location island : islands) {
            Weather weather = weatherProvider.get(island, Instant.now());
            if (weather != null) {
                System.out.println("Weather info for " + island.getName());
                System.out.println("Temperature: " + weather.getTemperature() + "°C");
                System.out.println("Humidity: " + weather.getHumidity() + "%");
                System.out.println("Clouds: " + weather.getClouds());
                System.out.println("Wind Speed: " + weather.getWindSpeed() + " m/s");
                System.out.println("Rain Probability: " + weather.getRainProb() + "%");
                System.out.println("Date Time: " + weather.getDt());

            } else {
                System.out.println("Failed to retrieve weather info for " + island.getName());
            }
        }
    }
}

