package dacd.blanco.control;

import dacd.blanco.model.Location;
import dacd.blanco.model.Weather;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class WeatherController {
    private final WeatherProvider weatherProvider;
    private final WeatherSender weatherSender;

    public WeatherController(WeatherProvider weatherProvider, WeatherSender weatherSender) {
        this.weatherProvider = weatherProvider;
        this.weatherSender = weatherSender;
    }

    public void execute() {
        List<Location> locationList = createLocationList();
        List<Instant> instantList = createInstantList();

        for (Location location : locationList) {
            for (Instant instant : instantList) {
                Weather weather = weatherProvider.get(location, instant);

                if (weather != null) {
                    System.out.println(weather);
                    System.out.println();
                    weatherSender.sendWeather(weather);
                } else {
                    System.out.println("No weather data found for " + location.getName() + " at " + instant);
                }
            }
        }
    }
    private List<Location> createLocationList() {
        return List.of(new Location("Lanzarote", 28.96302, -13.54769),
                new Location("Fuerteventura", 28.50038, -13.86272),
                new Location("Gran Canaria", 28.09973, -15.41343),
                new Location("Tenerife", 28.46824, -16.25462),
                new Location("El Hierro", 27.80628, -17.91578),
                new Location("La Palma", 28.68351, -17.76421),
                new Location("La Graciosa", 29.23147, -13.50341),
                new Location("La Gomera", 28.0916300, -17.1133100));
    }

    private List<Instant> createInstantList() {
        List<Instant> instants = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            instants.add(Instant.now().plus(i, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS).plus(12, ChronoUnit.HOURS));
        }
        return instants;
    }
}