package dacd.blanco.model;

import java.time.Instant;

public class Weather {
    private final double temperature;
    private final int humidity;
    private final int clouds;
    private final double windSpeed;
    private final double rainProb;
    private final Instant dt;

    public Weather(double temperature, int humidity, int clouds, double windSpeed, double rainProb, Instant dt) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.rainProb = rainProb;
        this.dt = dt;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
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

    public Instant getDt() {
        return dt;
    }
}