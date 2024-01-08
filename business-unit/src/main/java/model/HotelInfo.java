package model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HotelInfo {
    public static final SimpleDateFormat predictionTsFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static List<String> readDataMart(String filePath) {
        List<String> events = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                events.add(linea);
            }
        } catch (IOException e) {
            System.err.println("Fail reading file: " + new File(filePath).getAbsolutePath());
            e.printStackTrace();
        }
        return events;
    }

    public static String generateDataMartFileName() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return "datamart" + File.separator + "eventstore" + File.separator + currentDate.format(formatter)
                + File.separator + "all-events.events";
    }

    public static List<String> filterEventsByLocation(List<String> events, String location) {
        List<String> locationEvents = new ArrayList<>();
        for (String event : events) {
            if (event.contains("\"name\":\"" + location + "\"") || (event.contains("\"location\":\"" + location + "\""))) {
                locationEvents.add(event);
            }
        }
        return locationEvents;
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
                String predictionTs = extractPredictionTs(event);
                try {
                    Date predictionDate = predictionTsFormat.parse(predictionTs);
                    weather[5] = predictionDate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue;
                }
                weather[0] = parseDoubleValue(tokens[0]);
                weather[1] = parseDoubleValue(tokens[1]);
                weather[2] = parseDoubleValue(tokens[2]);
                weather[3] = parseDoubleValue(tokens[3]);
                weather[4] = parseDoubleValue(tokens[4].replaceAll("[^0-9.]", "").trim());

                weatherList.add(weather);
            }
        }

        return weatherList;
    }

    private static String extractPredictionTs(String event) {
        int startIdx = event.indexOf("predictionTs");
        if (startIdx == -1) {
            return null;
        }

        String[] tokens = event.substring(startIdx).split(",");
        return tokens[0].split(":")[1].replaceAll("[^0-9T-]", "").trim();
    }

    private static double parseDoubleValue(String token) {
        String[] parts = token.split(":");
        if (parts.length > 1) {
            return Double.parseDouble(parts[1].trim());
        }
        return 0.0;
    }

    public static List<String> sortHotelsByRate(List<String> locationEvents) {
        List<String> bestHotelsSorted = new ArrayList<>();

        for (String event : locationEvents) {
            int ratesIndex = event.indexOf("\"rates\"");
            int checkInIndex = event.indexOf("\"check_in\"");
            int checkOutIndex = event.indexOf("\"check_out\"");
            if (ratesIndex != -1 && checkInIndex != -1 && checkOutIndex != -1) {
                String ratesSubstring = event.substring(ratesIndex);
                String checkInSubstring = event.substring(checkInIndex);
                String checkOutSubstring = event.substring(checkOutIndex);

                String[] rateTokens = ratesSubstring.split("\"rate\":");
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

                    String checkIn = extractDateFromEvent(checkInSubstring);
                    String checkOut = extractDateFromEvent(checkOutSubstring);

                    bestHotelsSorted.add(hotelName + ": " + minRate + "€ - Check-in: " + checkIn + " - Check-out: " + checkOut);
                }
            }
        }

        Collections.sort(bestHotelsSorted, Comparator.comparingDouble(HotelInfo::extractRateFromHotelEntry));

        return bestHotelsSorted;
    }

    private static String extractDateFromEvent(String dateSubstring) {
        int startIndex = dateSubstring.indexOf(":\"") + 2;
        int endIndex = dateSubstring.indexOf("\",");
        if (startIndex != -1 && endIndex != -1) {
            return dateSubstring.substring(startIndex, endIndex);
        }
        return "";
    }

    private static String extractHotelNameFromEvent(String event) {
        int hotelNameIndex = event.indexOf("\"name\":\"");
        if (hotelNameIndex != -1) {
            int endIndex = event.indexOf("\",", hotelNameIndex);
            return event.substring(hotelNameIndex + "\"name\":\"".length(), endIndex);
        }
        return "";
    }

    private static double extractRateFromHotelEntry(String hotelEntry) {
        String numericPart = hotelEntry.replaceAll("[^\\d.]", "");
        if (!numericPart.isEmpty()) {
            return Double.parseDouble(numericPart);
        }
        return 0.0;
    }
}
