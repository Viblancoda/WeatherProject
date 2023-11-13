package dacd.blanco.control;

import dacd.blanco.model.Location;
import dacd.blanco.model.Weather;

import java.time.Instant;
import java.util.List;

public interface WeatherStore {
    void saveWeather(Weather weather);

    void loadWeather(Location location, Instant dt);
}