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

public class BusinessMain {
    private final Scanner scanner;

    public BusinessMain() {
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        BusinessMain businessMain = new BusinessMain();
        businessMain.chooseLocation();
    }

    public void chooseLocation() {
        String fileName = generateDataMartFileName();
        List<String> events = readDataMart(fileName);

        System.out.print("Ingrese el nombre de la ubicación (isla): ");
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
            double[] weather = new double[5];

            weather[0] = Double.parseDouble(tokens[0].split(":")[1].trim());
            weather[1] = Double.parseDouble(tokens[1].split(":")[1].trim());
            weather[2] = Double.parseDouble(tokens[2].split(":")[1].trim());
            weather[3] = Double.parseDouble(tokens[3].split(":")[1].trim());
            weather[4] = Double.parseDouble(tokens[4].split(":")[1].replaceAll("[^0-9.]", "").trim()); // Humedad

            weatherList.add(weather);
        }

        return weatherList;
    }

    private List<String> sortHotelsByRate(List<String> locationEvents) {
        List<String> bestHotelsSorted = new ArrayList<>();

        for (String event : locationEvents) {
            int ratesIndex = event.indexOf("\"rates\"");
            if (ratesIndex != -1) {
                String ratesSubstring = event.substring(ratesIndex);
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
                    bestHotelsSorted.add(hotelName + ": " + minRate);
                }
            }
        }

        Collections.sort(bestHotelsSorted, Comparator.comparingDouble(this::extractRateFromHotelEntry));

        return bestHotelsSorted;
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
        return Double.parseDouble(hotelEntry.split(":")[1].trim());
    }

    private static String generateDataMartFileName() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return "datamart" + File.separator + "eventstore" + File.separator + currentDate.format(formatter)
                + File.separator + "all-events.events";
    }

    private void displayWeather(List<double[]> weatherList) {
        System.out.println("\nDatos climáticos para cada evento:");
        for (double[] weather : weatherList) {
            System.out.println("Nubes: " + weather[0]);
            System.out.println("Velocidad del viento: " + weather[1] + " m/s");
            System.out.println("Probabilidad de lluvia: " + weather[2] + "%");
            System.out.println("Temperatura: " + weather[3] + "°C");
            System.out.println("Humedad: " + weather[4] + "\n");
        }
    }

    private void displaySortedHotels(List<String> sortedHotels) {
        System.out.println("\nHoteles ordenados por el precio más bajo:");
        for (String hotelEntry : sortedHotels) {
            System.out.println(hotelEntry + "€");
        }
    }
}
