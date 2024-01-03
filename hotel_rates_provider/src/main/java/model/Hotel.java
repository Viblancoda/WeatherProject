package model;

public class Hotel {
    private String location;
    private String name;
    private String hotelKey;

    public Hotel(String location, String name, String hotelKey) {
        this.location = location;
        this.name = name;
        this.hotelKey = hotelKey;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getHotelKey() {
        return hotelKey;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }
}
