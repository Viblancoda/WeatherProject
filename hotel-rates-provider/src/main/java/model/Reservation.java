package model;

import com.google.gson.JsonArray;
import java.time.Instant;

public class Reservation {
    private Hotel hotel;
    private String check_in;
    private String check_out;
    private JsonArray rates;
    private final Instant ts;
    private final String ss;


    public Reservation(Hotel hotel, String check_in, String check_out, JsonArray rates) {
        this.hotel = hotel;
        this.check_in = check_in;
        this.check_out = check_out;
        this.rates = rates;
        this.ts = Instant.now();
        this.ss = "hotel-rates-provider";
    }

    public void setCheck_in(String check_in) {
        this.check_in = check_in;
    }

    public void setCheck_out(String check_out) {
        this.check_out = check_out;
    }

    public void setRates(JsonArray rates) {
        this.rates = rates;
    }
}