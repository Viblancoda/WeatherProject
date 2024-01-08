package control;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.Reservation;
import model.Hotel;
import org.jsoup.Jsoup;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class OpenHotelMapProvider implements HotelProvider {

    @Override
    public Reservation getHotel(Hotel hotel, Reservation reservation) {
        Instant checkin = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant checkout = Instant.now().plus(4, ChronoUnit.DAYS);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String checkInDate = LocalDateTime.ofInstant(checkin, ZoneOffset.UTC).format(formatter);
        String checkOutDate = LocalDateTime.ofInstant(checkout, ZoneOffset.UTC).format(formatter);

        String apiUrl = "https://data.xotelo.com/api/rates?hotel_key=" + hotel.getHotelKey() +
                "&chk_in=" + checkInDate + "&chk_out=" + checkOutDate;

        try {
            String jsonString = Jsoup.connect(apiUrl).ignoreContentType(true).execute().body();

            Gson gson = new Gson();
            JsonObject hotelJson = gson.fromJson(jsonString, JsonObject.class);

            String chkIn = hotelJson.getAsJsonObject("result").get("chk_in").getAsString();
            String chkOut = hotelJson.getAsJsonObject("result").get("chk_out").getAsString();
            JsonArray ratesArray = hotelJson.getAsJsonObject("result").get("rates").getAsJsonArray();

            hotel.setName(hotel.getName());
            hotel.setLocation(hotel.getLocation());

            reservation.setCheck_in(chkIn);
            reservation.setCheck_out(chkOut);
            reservation.setRates(ratesArray);

            return reservation;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Reservation getHotelDetails(Hotel hotel) {
        Reservation reservation = new Reservation(hotel, "checkIn", "checkOut", new JsonArray());

        hotel.setName(hotel.getName());
        hotel.setLocation(hotel.getLocation());

        reservation.setRates(new JsonArray());

        try {
            return getHotel(hotel, reservation);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}