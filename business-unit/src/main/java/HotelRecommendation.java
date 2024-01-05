import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HotelRecommendation {

    public static void main(String[] args) {
        DataMartBuilder dataMartBuilder = new DataMartBuilder("datamart" + File.separator + "eventstore" + File.separator + "20240105" + File.separator);
        List<String> events = dataMartBuilder.readEvents();

        List<JsonObject> hotels = new ArrayList<>();
        List<JsonObject> weatherList = new ArrayList<>();

        for (String event : events) {
            JsonObject jsonObject = parseJsonObject(event);

            if (jsonObject.has("temperature")) {
                weatherList.add(jsonObject);
            } else if (jsonObject.has("hotel")) {
                hotels.add(jsonObject);
            }
        }

        HotelRecommendation recommendation = new HotelRecommendation();
        recommendation.generateRecommendation(hotels, weatherList);
    }

    public void generateRecommendation(List<JsonObject> hotels, List<JsonObject> weatherList) {
        List<JsonObject> filteredHotels = hotels.stream()
                .filter(hotel -> {
                    JsonArray ratesArray = hotel.getAsJsonArray("rates");
                    return !ratesArray.isEmpty() && ratesArray.get(0).isJsonObject();
                })
                .filter(hotel -> hotel.getAsJsonArray("rates").size() > 0) // Verificar que la lista de rates no esté vacía
                .filter(hotel -> hotel.getAsJsonArray("rates").get(0).getAsJsonObject().get("rate").getAsInt() < 100)
                .collect(Collectors.toList());

        filteredHotels.sort(Comparator.comparingInt(hotel -> hotel.getAsJsonArray("rates").get(0).getAsJsonObject().get("rate").getAsInt()));

        List<JsonObject> recommendedHotels = new ArrayList<>();

        for (JsonObject hotel : filteredHotels) {
            JsonArray ratesArray = hotel.getAsJsonArray("rates");

            if (ratesArray.size() > 0) {
                JsonObject weather = findWeatherForLocation(weatherList, hotel.getAsJsonPrimitive("location").getAsString());

                if (weather != null && weather.getAsJsonPrimitive("temperature").getAsDouble() >= 21
                        && weather.getAsJsonPrimitive("temperature").getAsDouble() <= 27
                        && weather.getAsJsonPrimitive("rainProb").getAsDouble() == 0.0) {
                    recommendedHotels.add(hotel);

                    if (recommendedHotels.size() == 3) {
                        break;
                    }
                }
            }
        }

        System.out.println("Recommended Hotels:");
        for (JsonObject hotel : recommendedHotels) {
            System.out.println((hotel.getAsJsonPrimitive("name") != null ? hotel.getAsJsonPrimitive("name").getAsString() : "Unknown") +
                    " - Rate: " + hotel.getAsJsonArray("rates").get(0).getAsJsonObject().get("rate").getAsInt());
        }
    }

    private JsonObject findWeatherForLocation(List<JsonObject> weatherList, String location) {
        return weatherList.stream()
                .filter(weather -> weather.getAsJsonObject("location").getAsJsonPrimitive("name").getAsString().equalsIgnoreCase(location))
                .findFirst()
                .orElse(null);
    }

    private static JsonObject parseJsonObject(String jsonString) {
      return new Gson().fromJson(jsonString, JsonObject.class);
    }
}