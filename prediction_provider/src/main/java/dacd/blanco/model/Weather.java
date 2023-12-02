package dacd.blanco.model;

import java.time.Instant;

public class Weather {
    private final Location location;
    private int clouds;
    private double windSpeed;
    private double rainProb;
    private double temperature;
    private int humidity;
    private Instant dt;
    private static Instant predictionDt = Instant.now();
    private static String ss = "event_store_builder";

    public Weather(Location location, int clouds, double windSpeed, double rainProb, double temperature, int humidity, Instant dt) {
        this.location = location;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.rainProb = rainProb;
        this.temperature = temperature;
        this.humidity = humidity;
        this.dt = dt;
    }

    public Location getLocation() {
        return location;
    }

    public int getClouds() {
        return clouds;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getRainProb() {
        return rainProb;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public Instant getDt() {
        return dt;
    }
}