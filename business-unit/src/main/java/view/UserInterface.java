package view;

import java.util.Scanner;
import java.util.List;
import java.util.Date;
import model.HotelInfo;

public class UserInterface {
    private final Scanner scanner;

    public UserInterface() {
        this.scanner = new Scanner(System.in);
    }

    public void selectLocation() {
        String fileName = HotelInfo.writeDataMartName();
        List<String> events = HotelInfo.readDataMart(fileName);

        System.out.print("Choose your island destination: ");
        String inputLocation = scanner.nextLine();

        List<String> locationEvents = HotelInfo.filterEventsByLocation(events, inputLocation);
        if (!locationEvents.isEmpty()) {
            displayWeather(HotelInfo.extractWeather(locationEvents));
            displaySortedHotels(HotelInfo.sortHotelsByRate(locationEvents));
        } else {
            System.out.println("No events for this island");
        }

        scanner.close();
    }

    private void displayWeather(List<double[]> weatherList) {
        System.out.println("\nWeather:");
        weatherList.forEach(weather -> {
            System.out.println("Date: " + HotelInfo.dateFormat.format(new Date((long) weather[5])));
            System.out.println("Clouds: " + weather[0]);
            System.out.println("Wind speed: " + weather[1] + " m/s");
            System.out.println("Rain probability: " + weather[2] + "%");
            System.out.println("Temperature: " + weather[3] + "°C");
            System.out.println("Humidity: " + weather[4] + "\n");
        });
    }

    private void displaySortedHotels(List<String> sortedHotels) {
        System.out.println("Hotels sorted by price:");
        sortedHotels.forEach(System.out::println);
    }
}