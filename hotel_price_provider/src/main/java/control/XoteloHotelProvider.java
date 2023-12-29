package control;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.Hotel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class XoteloHotelProvider implements HotelProvider {
    private static String apiKey;

    public XoteloHotelProvider(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public List<Hotel> getHotels(String islandName, Instant checkInDate, Instant checkOutDate) {
        List<Hotel> hotels = new ArrayList<>();

        try {
            String apiUrl = "https://data.xotelo.com/api/rates?hotel_key=" + apiKey +
                    "&chk_in=" + checkInDate + "&chk_out=" + checkOutDate;

            Document doc = Jsoup.connect(apiUrl).ignoreContentType(true).get();
            String jsonString = doc.body().text();

            Gson gson = new Gson();
            JsonObject hotelJson = gson.fromJson(jsonString, JsonObject.class);
            JsonObject result = hotelJson.getAsJsonObject("result");

            if (result != null) {
                JsonArray ratesArray = result.getAsJsonArray("rates");

                for (int i = 0; i < ratesArray.size(); i++) {
                    JsonObject rateObject = ratesArray.get(i).getAsJsonObject();

                    String hotelCode = rateObject.get("code").getAsString();
                    String hotelName = rateObject.get("name").getAsString();
                    double rate = rateObject.get("rate").getAsDouble();

                    hotels.add(new Hotel(islandName, hotelCode, hotelName, rate, checkInDate, checkOutDate));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return hotels;
    }
}