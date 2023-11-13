package dacd.blanco.control;

import dacd.blanco.model.Location;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
            WeatherController weatherController = new WeatherController(new OpenWeatherMapProvider(), new SQLiteWeatherStore());
            weatherController.execute();
    }
}