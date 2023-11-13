package dacd.blanco.control;
import dacd.blanco.model.Weather;
import dacd.blanco.model.Location;
import java.util.List;
import java.time.Instant;

public interface WeatherStore {
    void save(Weather weather);
    List<Weather> load(Location location, Instant instant);

}
