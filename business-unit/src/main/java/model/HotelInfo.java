package model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HotelInfo {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static List<String> readDataMart(String filePath) {
        List<String> events = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.lines().forEach(events::add);
        } catch (IOException e) {
            System.err.println("Fail reading file: " + new File(filePath).getAbsolutePath());
            e.printStackTrace();
        }
        return events;
    }

    public static String writeDataMartName() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return String.format("datamart/eventstore/%s/today.events", currentDate.format(formatter));
    }

    public static List<String> filterEventsByLocation(List<String> events, String location) {
        return events.stream()
                .filter(event -> event.contains("\"name\":\"" + location + "\"") || event.contains("\"location\":\"" + location + "\""))
                .toList();
    }

    public static List<double[]> extractWeather(List<String> locationEvents) {
        List<double[]> weatherList = new ArrayList<>();

        for (String event : locationEvents) {
            int startIdx = event.indexOf("clouds");
            if (startIdx == -1) {
                continue;
            }

            String[] tokens = event.substring(startIdx).split(",");
            if (tokens.length >= 5) {
                double[] weather = new double[6];
                String predictionTs = extractDatePrediction(event);
                try {
                    Date predictionDate = dateFormat.parse(predictionTs);
                    weather[5] = predictionDate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue;
                }
                for (int i = 0; i < 5; i++) {
                    weather[i] = parseDoubleValue(tokens[i]);
                }

                weatherList.add(weather);
            }
        }

        return weatherList;
    }

    private static String extractDatePrediction(String event) {
        int startIdx = event.indexOf("predictionTs");
        if (startIdx == -1) {
            return null;
        }

        String[] tokens = event.substring(startIdx).split(",");
        return tokens[0].split(":")[1].replaceAll("[^0-9T-]", "").trim();
    }

    private static double parseDoubleValue(String token) {
        String[] parts = token.split(":");
        return (parts.length > 1) ? Double.parseDouble(parts[1].trim()) : 0.0;
    }

    public static List<String> sortHotelsByRate(List<String> locationEvents) {
        return locationEvents.stream()
                .filter(event -> event.contains("\"rates\"") && event.contains("\"check_in\"") && event.contains("\"check_out\""))
                .map(event -> {
                    String[] rateTokens = event.substring(event.indexOf("\"rates\"")).split("\"rate\":");
                    List<Double> rates = new ArrayList<>();

                    for (int i = 1; i < rateTokens.length; i++) {
                        double rate = Double.parseDouble(rateTokens[i].split(",")[0].trim());
                        if (rate > 0) {
                            rates.add(rate);
                        }
                    }

                    if (!rates.isEmpty()) {
                        double minRate = Collections.min(rates);
                        String hotelName = extractHotelNameFromEvent(event);
                        String checkIn = extractDateFromEvent(event, "\"check_in\"");
                        String checkOut = extractDateFromEvent(event, "\"check_out\"");
                        return hotelName + ": " + minRate + "€ - Check-in: " + checkIn + " - Check-out: " + checkOut;
                    }
                    return "";
                })
                .filter(entry -> !entry.isEmpty())
                .sorted((hotel1, hotel2) -> {
                    double rate1 = extractRateFromHotel(hotel1);
                    double rate2 = extractRateFromHotel(hotel2);
                    return Double.compare(rate1, rate2);
                })
                .toList();
    }


    private static String extractHotelNameFromEvent(String hotel) {
        int hotelNameIndex = hotel.indexOf("\"name\":\"");
        if (hotelNameIndex != -1) {
            int endIndex = hotel.indexOf("\",", hotelNameIndex);
            return (endIndex != -1) ? hotel.substring(hotelNameIndex + "\"name\":\"".length(), endIndex) : "";
        }
        return "";
    }

    private static double extractRateFromHotel(String hotel) {
        String numericPart = hotel.replaceAll("[^\\d.]", "");
        return (!numericPart.isEmpty()) ? Double.parseDouble(numericPart) : 0.0;
    }

    private static String extractDateFromEvent(String event, String field) {
        int startIndex = event.indexOf(field) + field.length() + 2;
        int endIndex = event.indexOf("\"", startIndex);
        return (startIndex != -1 && endIndex != -1) ? event.substring(startIndex, endIndex).trim() : "";
    }
}