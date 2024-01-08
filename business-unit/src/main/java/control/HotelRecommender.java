package control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HotelRecommender {
    private final Scanner scanner;
    private static final SimpleDateFormat predictionTsFormat = new SimpleDateFormat("yyyy-MM-dd");
    public HotelRecommender() {
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        HotelRecommender hotelRecommender = new HotelRecommender();
        hotelRecommender.chooseLocation();
    }

    public void chooseLocation() {
        String fileName = generateDataMartFileName();
        List<String> events = readDataMart(fileName);

        System.out.print("Ingrese el nombre de la isla que desea: ");
        String inputLocation = scanner.nextLine();

        List<String> locationEvents = filterEventsByLocation(events, inputLocation);
        if (!locationEvents.isEmpty()) {

            List<double[]> weatherList = extractWeather(locationEvents);
            displayWeather(weatherList);

            List<String> sortedHotels = sortHotelsByRate(locationEvents);
            displaySortedHotels(sortedHotels);
        } else {
            System.out.println("No hay eventos para la ubicación ingresada.");
        }

        scanner.close();
    }

    private List<String> readDataMart(String filePath) {
        List<String> events = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                events.add(linea);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + new File(filePath).getAbsolutePath());
            e.printStackTrace();
        }
        return events;
    }

    private List<String> filterEventsByLocation(List<String> events, String location) {
        List<String> locationEvents = new ArrayList<>();
        for (String event : events) {
            if (event.contains("\"name\":\"" + location + "\"") || (event.contains("\"location\":\"" + location + "\""))) {
                locationEvents.add(event);
            }
        }
        return locationEvents;
    }

    private List<double[]> extractWeather(List<String> locationEvents) {
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

    private String extractPredictionTs(String event) {
        int startIdx = event.indexOf("predictionTs");
        if (startIdx == -1) {
            return null;
        }

        String[] tokens = event.substring(startIdx).split(",");
        return tokens[0].split(":")[1].replaceAll("[^0-9T-]", "").trim();
    }

    private double parseDoubleValue(String token) {
        String[] parts = token.split(":");
        if (parts.length > 1) {
            return Double.parseDouble(parts[1].trim());
        }
        return 0.0;
    }

    private List<String> sortHotelsByRate(List<String> locationEvents) {
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

        Collections.sort(bestHotelsSorted, Comparator.comparingDouble(this::extractRateFromHotelEntry));

        return bestHotelsSorted;
    }

    private String extractDateFromEvent(String dateSubstring) {
        int startIndex = dateSubstring.indexOf(":\"") + 2;
        int endIndex = dateSubstring.indexOf("\",");
        if (startIndex != -1 && endIndex != -1) {
            return dateSubstring.substring(startIndex, endIndex);
        }
        return "";
    }

    private String extractHotelNameFromEvent(String event) {
        int hotelNameIndex = event.indexOf("\"name\":\"");
        if (hotelNameIndex != -1) {
            int endIndex = event.indexOf("\",", hotelNameIndex);
            return event.substring(hotelNameIndex + "\"name\":\"".length(), endIndex);
        }
        return "";
    }

    private double extractRateFromHotelEntry(String hotelEntry) {
        String numericPart = hotelEntry.replaceAll("[^\\d.]", "");
        if (!numericPart.isEmpty()) {
            return Double.parseDouble(numericPart);
        }
        return 0.0;
    }

    private static String generateDataMartFileName() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return "datamart" + File.separator + "eventstore" + File.separator + currentDate.format(formatter)
                + File.separator + "all-events.events";
    }

    private void displayWeather(List<double[]> weatherList) {
        System.out.println("\nDatos climáticos:");
        for (double[] weather : weatherList) {
            System.out.println("Fecha de predicción: " + predictionTsFormat.format(new Date((long) weather[5])));
            System.out.println("Nubes: " + weather[0]);
            System.out.println("Velocidad del viento: " + weather[1] + " m/s");
            System.out.println("Probabilidad de lluvia: " + weather[2] + "%");
            System.out.println("Temperatura: " + weather[3] + "°C");
            System.out.println("Humedad: " + weather[4] + "\n");
        }
    }

    private void displaySortedHotels(List<String> sortedHotels) {
        System.out.println("\nHoteles ordenados desde el más barato:");
        for (String hotelEntry : sortedHotels) {
            System.out.println(hotelEntry);
        }
    }
}
