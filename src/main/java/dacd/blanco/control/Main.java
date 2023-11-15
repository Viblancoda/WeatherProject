package dacd.blanco.control;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        WeatherController weatherController = new WeatherController(new OpenWeatherMapProvider(), new SQLiteWeatherStore());

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                weatherController.execute();
            }
        }, 0, TimeUnit.HOURS.toMillis(6));
    }
}