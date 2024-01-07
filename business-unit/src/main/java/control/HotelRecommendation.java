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

public class HotelRecommendation {
    private final Scanner scanner;

    public HotelRecommendation() {
        this.scanner = new Scanner(System.in);
    }

    public void chooseLocation() {
        String fileName = generateDatamartFileName();
        List<String> eventos = readDataMart(fileName);

        System.out.print("Ingrese el nombre de la ubicación (isla): ");
        String ubicacionInput = scanner.nextLine();

        List<String> locationEvents = filterEventsByLocation(eventos, ubicacionInput);
        if (!locationEvents.isEmpty()) {
            System.out.println("\nEventos para la ubicación " + ubicacionInput + ":");
            locationEvents.forEach(System.out::println);

            List<double[]> climaticDataList = extractWeatherData(locationEvents);
            displayClimaticData(climaticDataList);

            List<String> hotelesOrdenados = sortHotelsByRate(locationEvents);
            displaySortedHotels(hotelesOrdenados);
        } else {
            System.out.println("No hay eventos para la ubicación ingresada.");
        }

        scanner.close();
    }

    private List<String> readDataMart(String filePath) {
        List<String> eventos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                eventos.add(linea);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + new File(filePath).getAbsolutePath());
            e.printStackTrace();
        }
        return eventos;
    }

    private List<String> filterEventsByLocation(List<String> eventos, String ubicacion) {
        List<String> eventosUbicacion = new ArrayList<>();
        for (String evento : eventos) {
            if (evento.contains("\"name\":\"" + ubicacion + "\"") || (evento.contains("\"location\":\"" + ubicacion + "\""))) {
                eventosUbicacion.add(evento);
            }
        }
        return eventosUbicacion;
    }

    private List<double[]> extractWeatherData(List<String> locationEvents) {
        List<double[]> weatherList = new ArrayList<>();

        for (String evento : locationEvents) {
            int startIdx = evento.indexOf("clouds");
            if (startIdx == -1) {
                continue;
            }

            String[] tokens = evento.substring(startIdx).split(",");
            double[] climaticData = new double[5];

            climaticData[0] = Double.parseDouble(tokens[0].split(":")[1].trim());
            climaticData[1] = Double.parseDouble(tokens[1].split(":")[1].trim());
            climaticData[2] = Double.parseDouble(tokens[2].split(":")[1].trim());
            climaticData[3] = Double.parseDouble(tokens[3].split(":")[1].trim());
            climaticData[4] = Double.parseDouble(tokens[4].split(":")[1].replaceAll("[^0-9.]", "").trim()); // Humedad

            weatherList.add(climaticData);
        }

        return weatherList;
    }

    private List<String> sortHotelsByRate(List<String> locationEvents) {
        List<String> bestHotelsSorted = new ArrayList<>();

        for (String evento : locationEvents) {
            int ratesIndex = evento.indexOf("\"rates\"");
            if (ratesIndex != -1) {
                String ratesSubstring = evento.substring(ratesIndex);
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
                    String hotelName = extractHotelNameFromEvent(evento);
                    bestHotelsSorted.add(hotelName + ": " + minRate);
                }
            }
        }

        Collections.sort(bestHotelsSorted, Comparator.comparingDouble(this::extractRateFromHotelEntry));

        return bestHotelsSorted;
    }

    private String extractHotelNameFromEvent(String evento) {
        int hotelNameIndex = evento.indexOf("\"name\":\"");
        if (hotelNameIndex != -1) {
            int endIndex = evento.indexOf("\",", hotelNameIndex);
            return evento.substring(hotelNameIndex + "\"name\":\"".length(), endIndex);
        }
        return "";
    }

    private double extractRateFromHotelEntry(String hotelEntry) {
        return Double.parseDouble(hotelEntry.split(":")[1].trim());
    }

    private static String generateDatamartFileName() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return "datamart" + File.separator + "eventstore" + File.separator + currentDate.format(formatter)
                + File.separator + "events.events";
    }

    private void displayClimaticData(List<double[]> climaticDataList) {
        System.out.println("\nDatos climáticos para cada evento:");
        for (double[] climaticData : climaticDataList) {
            System.out.println("Nubes: " + climaticData[0]);
            System.out.println("Velocidad del viento: " + climaticData[1] + " m/s");
            System.out.println("Probabilidad de lluvia: " + climaticData[2] + "%");
            System.out.println("Temperatura: " + climaticData[3] + "°C");
            System.out.println("Humedad: " + climaticData[4] + "\n");
        }
    }

    private void displaySortedHotels(List<String> hotelesOrdenados) {
        System.out.println("\nHoteles ordenados por el precio más bajo:");
        for (String hotelEntry : hotelesOrdenados) {
            System.out.println(hotelEntry);
        }
    }
}
