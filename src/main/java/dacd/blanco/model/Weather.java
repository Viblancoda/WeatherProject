package dacd.blanco.model;

import java.time.Instant;

public class Weather {
    private int clouds;
    private double windSpeed;
    private double rainProb;
    private double temperature;
    private int humidity;
    private Instant dt;

    public Weather(int clouds, double windSpeed, double rainProb, double temperature, int humidity, Instant dt) {
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.rainProb = rainProb;
        this.temperature = temperature;
        this.humidity = humidity;
        this.dt = dt;
    }

    public int getClouds() {
        return clouds;
    }

    public void setClouds(int clouds) {
        this.clouds = clouds;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getRainProb() {
        return rainProb;
    }

    public void setRainProb(double rainProb) {
        this.rainProb = rainProb;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public Instant getDt() {
        return dt;
    }

    public void setDt(Instant dt) {
        this.dt = dt;
    }
}
