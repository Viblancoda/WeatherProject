package view;

import java.util.Scanner;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import model.HotelRecommender;

public class UserInterface {
    private final Scanner scanner;

    public UserInterface() {
        this.scanner = new Scanner(System.in);
    }

    public void chooseLocation() {
        String fileName = HotelRecommender.generateDataMartFileName();
        List<String> events = HotelRecommender.readDataMart(fileName);

        System.out.print("Choose your island destination: ");
        String inputLocation = scanner.nextLine();

        List<String> locationEvents = HotelRecommender.filterEventsByLocation(events, inputLocation);
        if (!locationEvents.isEmpty()) {

            List<double[]> weatherList = HotelRecommender.extractWeather(locationEvents);
            displayWeather(weatherList);

            List<String> sortedHotels = HotelRecommender.sortHotelsByRate(locationEvents);
            displaySortedHotels(sortedHotels);
        } else {
            System.out.println("No events for this island");
        }

        scanner.close();
    }

    private void displayWeather(List<double[]> weatherList) {
        System.out.println("\nWeather:");
        for (double[] weather : weatherList) {
            System.out.println("Prediction date: " + HotelRecommender.predictionTsFormat.format(new Date((long) weather[5])));
            System.out.println("Clouds: " + weather[0]);
            System.out.println("Wind speed: " + weather[1] + " m/s");
            System.out.println("Rain probability: " + weather[2] + "%");
            System.out.println("Temperature: " + weather[3] + "°C");
            System.out.println("Humidity: " + weather[4] + "\n");
        }
    }

    private void displaySortedHotels(List<String> sortedHotels) {
        System.out.println("\nHotels sorted by price:");
        for (String hotelEntry : sortedHotels) {
            System.out.println(hotelEntry);
        }
    }
}
