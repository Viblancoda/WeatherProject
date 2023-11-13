package dacd.blanco.control;

import dacd.blanco.model.Location;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class Main {
    public static void main(String[] args) {
        Location lanzarote = new Location("Lanzarote", 28.96302, -13.54769);
        Location fuerteventura = new Location("Fuerteventura", 28.50038, -13.86272);
        Location GranCanaria = new Location("Gran Canaria", 28.09973, -15.41343);
        Location tenerife = new Location("Tenerife", 28.46824, -16.25462);
        Location hierro = new Location("El hierro", 27.80628, -17.91578);
        Location gomera = new Location("La Gomera", 28.09163, -17.11331);
        Location palma = new Location("La Palma", 28.68351, -17.76421);
        Location graciosa = new Location("La Graciosa", 29.23147, -13.50341);

        ArrayList<Location> islands = new ArrayList<>();
        islands.add(lanzarote);
        islands.add(fuerteventura);
        islands.add(GranCanaria);
        islands.add(tenerife);
        islands.add(hierro);
        islands.add(gomera);
        islands.add(palma);
        islands.add(graciosa);

        String dbPath = "C:\\Users\\vituk\\OneDrive\\Escritorio\\SQLite\\weather.db";
        SQLiteWeatherStore weatherStore = new SQLiteWeatherStore(dbPath);

        WeatherController weatherController = new WeatherController();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> weatherController.displayAndSaveWeatherInfo(islands), 0, 6, TimeUnit.HOURS);
    }
}