package control;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Hotel;
import model.Rate;
import model.Reservation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OpenHotelMapProvider implements HotelProvider {

    @Override
    public Reservation getHotel(Hotel hotel, Reservation reservation) {
        try {
            String apiUrl = "https://data.xotelo.com/api/rates?hotel_key=" + hotel.getHotelKey() +
                    "&chk_in=" + reservation.getFormattedCheck_in() + "&chk_out=" + reservation.getFormattedCheck_out();

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(connection.getInputStream());

                JsonNode ratesNode = jsonNode.get("result").get("rates");
                List<Rate> rates = parseRates(ratesNode);

                reservation.setRates(rates);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return reservation;
    }

    private List<Rate> parseRates(JsonNode ratesNode) {
        List<Rate> rates = new ArrayList<>();
        for (JsonNode rateNode : ratesNode) {
            String code = rateNode.get("code").asText();
            String name = rateNode.get("name").asText();
            double rate = rateNode.get("rate").asDouble();

            rates.add(new Rate(code, name, rate));
        }
        return rates;
    }
}
