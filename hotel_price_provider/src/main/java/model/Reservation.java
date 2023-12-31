package model;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import java.time.format.DateTimeFormatter;


public class Reservation {
    private Instant check_in;
    private Instant check_out;
    private List<Rate> rates;
    private final Instant ts;
    private final String ss;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Reservation(List<Rate> rates) {
        this.check_in = Instant.now();
        this.check_out = Instant.now().plus(5, ChronoUnit.DAYS);
        this.rates = rates;
        this.ts = Instant.now();
        this.ss = "hotel_price_provider";
    }

    public Reservation(List<Rate> rates, Instant check_in, Instant check_out) {
        this.check_in = check_in;
        this.check_out = check_out;
        this.rates = rates;
        this.ts = Instant.now();
        this.ss = "hotel_price_provider";
    }

    // Getters and setters

    public String getFormattedCheck_in() {
        return check_in.atZone(ZoneId.systemDefault()).toLocalDate().format(DATE_FORMATTER);
    }

    public String getFormattedCheck_out() {
        return check_out.atZone(ZoneId.systemDefault()).toLocalDate().format(DATE_FORMATTER);
    }

    public List<Rate> getRates() {
        return rates;
    }

    public void setRates(List<Rate> rates) {
        this.rates = rates;
    }

    public Instant getTs() {
        return ts;
    }

    public String getSs() {
        return ss;
    }
}
