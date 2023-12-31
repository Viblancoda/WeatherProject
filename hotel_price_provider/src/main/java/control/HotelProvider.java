package control;

import model.Hotel;
import model.Reservation;

import java.util.List;


public interface HotelProvider {
    Reservation getHotel(Hotel hotel, Reservation reservation);
}
