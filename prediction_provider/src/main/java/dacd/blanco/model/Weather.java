package dacd.blanco.model;

import java.time.Instant;

public class Weather {
    private final Location location;
    private int clouds;
    private double windSpeed;
    private double rainProb;
    private double temperature;
    private int humidity;
    private Instant predictionTs;
    private static Instant ts;
    private static String ss;

    public Weather(Location location, int clouds, double windSpeed, double rainProb, double temperature, int humidity, Instant predictionTs) {
        this.location = location;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.rainProb = rainProb;
        this.temperature = temperature;
        this.humidity = humidity;
        this.predictionTs = predictionTs;
        this.ts = Instant.now();
        this.ss = "prediction_provider";
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

    public Instant getPredictionTs() {
        return predictionTs;
    }

    public static Instant getTs() {
        return ts;
    }

    public static String getSs() {
        return ss;
    }
}