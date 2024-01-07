package control;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
import java.util.stream.Collectors;

public class HotelRecommendation {
    private final Scanner scanner;

    public HotelRecommendation() {
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        HotelRecommendation hotelRecommendation = new HotelRecommendation();
        hotelRecommendation.chooseLocation();
    }

    public void chooseLocation() {
        String fileName = generateDatamartFileName();
        List<String> eventos = readDataMart(fileName);

        System.out.print("Ingrese el nombre de la ubicación (isla): ");
        String ubicacionInput = scanner.nextLine();

        List<String> eventosUbicacion = filterEventsByLocation(eventos, ubicacionInput);
        if (!eventosUbicacion.isEmpty()) {
            System.out.println("\nEventos para la ubicación " + ubicacionInput + ":");
            eventosUbicacion.forEach(System.out::println);

            // Datos climáticos
            List<double[]> climaticDataList = extractWeatherData(eventosUbicacion);
            displayClimaticData(climaticDataList);

            // Hoteles ordenados por rate
            List<String> hotelesOrdenados = sortHotelsByRate(eventosUbicacion);
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
            if (evento.contains("\"name\":\"" + ubicacion + "\"")) {
                eventosUbicacion.add(evento);
            }
        }
        return eventosUbicacion;
    }

    private List<double[]> extractWeatherData(List<String> eventosUbicacion) {
        List<double[]> climaticDataList = new ArrayList<>();

        for (String evento : eventosUbicacion) {
            int startIdx = evento.indexOf("clouds");
            if (startIdx == -1) {
                continue;
            }

            String[] tokens = evento.substring(startIdx).split(",");
            double[] climaticData = new double[5];

            climaticData[0] = Double.parseDouble(tokens[0].split(":")[1].trim()); // Nubosidad
            climaticData[1] = Double.parseDouble(tokens[1].split(":")[1].trim()); // Velocidad del viento
            climaticData[2] = Double.parseDouble(tokens[2].split(":")[1].trim()); // Probabilidad de lluvia
            climaticData[3] = Double.parseDouble(tokens[3].split(":")[1].trim()); // Temperatura
            climaticData[4] = Double.parseDouble(tokens[4].split(":")[1].replaceAll("[^0-9.]", "").trim()); // Humedad

            climaticDataList.add(climaticData);
        }

        return climaticDataList;
    }


    private List<String> sortHotelsByRate(List<String> eventosUbicacion) {
        List<String> hoteles = new ArrayList<>();

        for (String evento : eventosUbicacion) {
            JsonObject json = JsonParser.parseString(evento).getAsJsonObject();
            JsonElement rateElement = json.getAsJsonObject().get("rate");
            if (rateElement != null && !rateElement.isJsonNull()) {
                String hotelInfo = rateElement.getAsString(); // Agrega la información del hotel incluyendo la tasa (rate)
                hoteles.add(hotelInfo);
            }
        }

        if (!hoteles.isEmpty()) {
            // Filtra las listas de rates no vacías
            hoteles = hoteles.stream()
                    .filter(hotelInfo -> {
                        JsonObject json = JsonParser.parseString(hotelInfo).getAsJsonObject();
                        JsonElement rateElement = json.getAsJsonPrimitive("rate");
                        return rateElement != null && !rateElement.isJsonNull();
                    })
                    .collect(Collectors.toList());

            // Ordena la lista de hoteles por la tasa (rate) de menor a mayor
            Collections.sort(hoteles, Comparator.comparingDouble(hotelInfo -> {
                JsonObject json = JsonParser.parseString(hotelInfo).getAsJsonObject();
                return json.getAsJsonPrimitive("rate").getAsDouble();
            }));

            return hoteles;
        } else {
            System.out.println("No hay hoteles disponibles para la ubicación especificada.");
            return Collections.emptyList();
        }
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
        System.out.println("\nHoteles ordenados por rate de menor a mayor:");
        hotelesOrdenados.forEach(System.out::println);
    }
}
