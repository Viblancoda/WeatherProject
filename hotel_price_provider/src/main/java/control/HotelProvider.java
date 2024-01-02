package control;

import model.Hotel;
import model.Reservation;

public interface HotelProvider {
    Reservation getHotel(Hotel hotel, Reservation reservation);
}
