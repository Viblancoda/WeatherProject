package dacd.blanco.model;

public class Weather {
    private final double temperature;
    private final double humidity;
    private final int clouds;
    private final double windSpeed;
    private final double rainProb;

    public Weather(double temperature, double humidity, int clouds, double windSpeed, double rainProb) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.rainProb = rainProb;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
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
}