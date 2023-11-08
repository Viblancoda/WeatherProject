package dacd.blanco.model;

public class Weather {
    private final String cod;
    private final int message;
    private final int cnt;

    public Weather(String cod, int message, int cnt) {
        this.cod = cod;
        this.message = message;
        this.cnt = cnt;
    }

    public String getCod() {
        return cod;
    }

    public int getMessage() {
        return message;
    }

    public int getCnt() {
        return cnt;
    }
}
