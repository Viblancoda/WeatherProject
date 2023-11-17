package dacd.blanco.control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;

import dacd.blanco.model.Location;
import dacd.blanco.model.Weather;

public class SQLiteWeatherStore implements WeatherStore {

    private static final String DATABASE_URL = "jdbc:sqlite:C:/Users/vituk/OneDrive/Escritorio/SQLite/jaco.db";

    public SQLiteWeatherStore() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             Statement statement = connection.createStatement()) {

            // Crear una tabla para cada isla
            for (Location location : createLocationList()) {
                String tableName = location.getName().toLowerCase().replace(" ", "_") + "_weather";
                String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                        "clouds INTEGER," +
                        "wind REAL," +
                        "pop REAL," +
                        "temperature REAL," +
                        "humidity INTEGER," +
                        "dt TEXT" +
                        ")";
                statement.executeUpdate(createTableSQL);
                System.out.println("Table created successfully for " + location.getName());
            }

            System.out.println("All tables created successfully.");

        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database", e);
        }
    }

    @Override
    public void saveWeather(Weather weather) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = createInsertStatement(connection, weather)) {

            preparedStatement.executeUpdate();
            System.out.println("Weather data saved successfully.");

        } catch (SQLException e) {
            throw new RuntimeException("Error saving weather data", e);
        }
    }

    @Override
    public void loadWeather(Location location, Instant instant) {
        WeatherProvider weatherProvider = new OpenWeatherMapProvider();
        Weather weather = weatherProvider.get(location, instant);

        if (weather != null) {
            saveWeather(weather);
        } else {
            System.out.println("No weather data found for " + location.getName() + " at " + instant);
        }
    }

    @Override
    public boolean exists(Location location, Instant dt) {
        String tableName = location.getName().toLowerCase().replace(" ", "_") + "_weather";
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE dt = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, dt.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error checking if weather data exists", e);
        }
    }

    @Override
    public void updateWeather(Weather weather) {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement preparedStatement = createUpdateStatement(connection, weather)) {

            preparedStatement.executeUpdate();
            System.out.println("Weather data updated successfully.");

        } catch (SQLException e) {
            throw new RuntimeException("Error updating weather data", e);
        }
    }

    private PreparedStatement createInsertStatement(Connection connection, Weather weather) throws SQLException {
        String tableName = weather.getLocation().getName().toLowerCase().replace(" ", "_") + "_weather";
        String insertWeatherSQL =
                "INSERT INTO " + tableName + " (clouds, wind, pop, temperature, humidity, dt) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(insertWeatherSQL);

        preparedStatement.setInt(1, weather.getClouds());
        preparedStatement.setDouble(2, weather.getWindSpeed());
        preparedStatement.setDouble(3, weather.getRainProb());
        preparedStatement.setDouble(4, weather.getTemperature());
        preparedStatement.setInt(5, weather.getHumidity());
        preparedStatement.setString(6, weather.getDt().toString());

        return preparedStatement;
    }

    private PreparedStatement createUpdateStatement(Connection connection, Weather weather) throws SQLException {
        String tableName = weather.getLocation().getName().toLowerCase().replace(" ", "_") + "_weather";
        String updateWeatherSQL =
                "UPDATE " + tableName + " SET clouds=?, wind=?, pop=?, temperature=?, humidity=? WHERE dt=?";

        PreparedStatement preparedStatement = connection.prepareStatement(updateWeatherSQL);

        preparedStatement.setInt(1, weather.getClouds());
        preparedStatement.setDouble(2, weather.getWindSpeed());
        preparedStatement.setDouble(3, weather.getRainProb());
        preparedStatement.setDouble(4, weather.getTemperature());
        preparedStatement.setInt(5, weather.getHumidity());
        preparedStatement.setString(6, weather.getDt().toString());

        return preparedStatement;
    }

    private List<Location> createLocationList() {
        return List.of(new Location("Lanzarote", 28.96302, -13.54769),
                new Location("Fuerteventura", 28.50038, -13.86272),
                new Location("Gran Canaria", 28.09973, -15.41343),
                new Location("Tenerife", 28.46824, -16.25462),
                new Location("El Hierro", 27.80628, -17.91578),
                new Location("La Palma", 28.68351, -17.76421),
                new Location("La Graciosa", 29.23147, -13.50341),
                new Location("La Gomera", 28.0916300, -17.1133100));
    }
}