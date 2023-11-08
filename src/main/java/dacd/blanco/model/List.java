package dacd.blanco.model;

public class List {
    private final Temperature temperature;
    private final Humidity humidity;
    private final Rain rainProb;
    private final Wind windSpeed;
    private final Cloud clouds;

    public List(Temperature temperature, Humidity humidity, Rain rainProb, Wind windSpeed, Cloud clouds) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.rainProb = rainProb;
        this.windSpeed = windSpeed;
        this.clouds = clouds;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public Humidity getHumidity() {
        return humidity;
    }

    public Rain getRainProb() {
        return rainProb;
    }

    public Wind getWindSpeed() {
        return windSpeed;
    }

    public Cloud getClouds() {
        return clouds;
    }
}
