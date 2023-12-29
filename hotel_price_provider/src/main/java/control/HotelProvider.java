package control;

import model.Hotel;

import java.time.Instant;
import java.util.List;

public interface HotelProvider {
    List<Hotel> getHotels(String location, Instant checkInDate, Instant checkOutDate);
}