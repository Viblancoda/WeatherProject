package dacd.blanco.control;
import java.time.Instant;
import dacd.blanco.model.Weather;
import dacd.blanco.model.Location;

public interface WeatherProvider {
    Weather get(Location location, Instant instant);
}