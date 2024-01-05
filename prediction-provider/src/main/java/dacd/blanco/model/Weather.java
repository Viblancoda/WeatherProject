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
    private final Instant ts;
    private final String ss;

    public Weather(Location location, int clouds, double windSpeed, double rainProb, double temperature, int humidity, Instant predictionTs) {
        this.location = location;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.rainProb = rainProb;
        this.temperature = temperature;
        this.humidity = humidity;
        this.predictionTs = predictionTs;
        this.ts = Instant.now();
        this.ss = "prediction-provider";
    }
}