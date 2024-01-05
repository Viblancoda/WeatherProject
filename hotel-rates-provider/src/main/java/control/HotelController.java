package control;
import model.Hotel;
import model.Reservation;

import java.util.List;

import java.util.ArrayList;


public class HotelController {
    private final HotelSender hotelSender;

    public HotelController(HotelSender hotelSender) {
        this.hotelSender = hotelSender;
    }

    public void execute() {
        List<Reservation> reservationList = new ArrayList<>();

        for (Hotel hotel : createHotelList()) {
            Reservation reservation = new OpenHotelMapProvider().getHotelDetails(hotel);
            if (reservation != null) {
                reservationList.add(reservation);
            }
        }

        for (Reservation reservation : reservationList) {
            System.out.println("Reservation details: " + reservation);
            hotelSender.send(reservation);
        }
    }


    private List<Hotel> createHotelList() {
        return List.of(new Hotel("Gran Canaria", "Abora Buenaventura", "g562819-d289606"),
                new Hotel("Gran Canaria", "Lopesan Costa Meloneras", "g2089121-d241729"),
                new Hotel("Gran Canaria", "Exe Las Palmas", "g187472-d507826"),
                new Hotel("Tenerife", "Hotel Botánico", "g187481-d252888"),
                new Hotel("Tenerife", "H10 Atlantic", "g315919-d15327583"),
                new Hotel("Tenerife", "Iberostar Boungaville", "g662606-d296925"),
                new Hotel("Lanzarote", "Hotel Beatriz Costa and Spa", "g659633-d291300"),
                new Hotel("Lanzarote", "H10 Rubicon Palace", "g652121-d289259"),
                new Hotel("Lanzarote", "Gloria Izaro Club Hotel", "g662290-d4716670"),
                new Hotel("Fuerteventura", "AluaSuites", "g783945-d237051"),
                new Hotel("Fuerteventura", "Melia Fuerteventura", "g580322-d573425"),
                new Hotel("Fuerteventura", "Fuerteventura Beach Club", "g658907-d500715"),
                new Hotel("La Gomera", "Hotel Playa Calera", "g21309597-d1379967"),
                new Hotel("La Gomera", "Hotel Gran Rey", "g674782-d616495"),
                new Hotel("La Gomera", "Hotel Rural Tamahuche", "g1024999-d616810"),
                new Hotel("La Palma", "Hotel Las Olas", "g642213-d488944"),
                new Hotel("La Palma", "H10 Taburiente Playa", "g659966-d289252"),
                new Hotel("La Palma", "Parador de La Palma", "g642213-d482745"),
                new Hotel("La Graciosa", "Evita Beach", "g1190272-d2645782"),
                new Hotel("La Graciosa", "La Pardela", "g3360203-d25244761"),
                new Hotel("La Graciosa", "El Marinero Avenida", "g3360203-d17628872"),
                new Hotel("El Hierro", "Parador El Hierro", "g187474-d277394"),
                new Hotel("El Hierro", "ApartaHotel Boomerang", "g187474-d1497072"),
                new Hotel("El Hierro", "Punta Grande", "g2139290-d627753")
        );
    }

}