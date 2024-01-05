package dacd.blanco.control;

import dacd.blanco.model.Weather;

public interface WeatherSender {
    void sendWeather(Weather weather);
}