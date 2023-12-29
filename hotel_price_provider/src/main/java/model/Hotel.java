package model;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private final String location;
    private final String hotelCode;
    private final String hotelName;
    private final double rate;
    private final Instant checkInDate;
    private final Instant checkOutDate;

    public Hotel(String location, String hotelCode, String hotelName, double rate, Instant checkInDate, Instant checkOutDate) {
        this.location = location;
        this.hotelCode = hotelCode;
        this.hotelName = hotelName;
        this.rate = rate;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public String getLocation() {
        return location;
    }

    public String getHotelCode() {
        return hotelCode;
    }

    public String getHotelName() {
        return hotelName;
    }

    public double getRate() {
        return rate;
    }

    public Instant getCheckInDate() {
        return checkInDate;
    }

    public Instant getCheckOutDate() {
        return checkOutDate;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "location=" + location +
                ", hotelCode='" + hotelCode + '\'' +
                ", hotelName='" + hotelName + '\'' +
                ", rate=" + rate +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                '}';
    }
}