package dacd.blanco.control;

import java.time.Instant;

import dacd.blanco.model.Location;
import dacd.blanco.model.Weather;

public interface WeatherProvider {
    Weather get(Location location, Instant instant);
}