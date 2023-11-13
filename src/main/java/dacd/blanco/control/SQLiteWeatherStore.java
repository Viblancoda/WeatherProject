package dacd.blanco.control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;

import dacd.blanco.model.Location;
import dacd.blanco.model.Weather;

public class SQLiteWeatherStore implements WeatherStore {

    private static final String DATABASE_URL = "jdbc:sqlite:C:/Users/vituk/OneDrive/Escritorio/SQLite/weather.db";

    public SQLiteWeatherStore() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             Statement statement = connection.createStatement()) {

            String createTableSQL = "CREATE TABLE IF NOT EXISTS weather (" +
                    "name TEXT," +
                    "clouds INTEGER," +
                    "wind REAL," +
                    "pop REAL," +
                    "temperature REAL," +
                    "humidity INTEGER," +
                    "dt TEXT" +
                    ")";
            statement.executeUpdate(createTableSQL);

            System.out.println("Table created successfully.");

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

    private PreparedStatement createInsertStatement(Connection connection, Weather weather) throws SQLException {
        String insertWeatherSQL = "INSERT INTO weather (name, clouds, wind, pop, temperature, humidity, dt) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertWeatherSQL);

        preparedStatement.setString(1, weather.getLocation().getName());
        preparedStatement.setInt(2, weather.getClouds());
        preparedStatement.setDouble(3, weather.getWindSpeed());
        preparedStatement.setDouble(4, weather.getRainProb());
        preparedStatement.setDouble(5, weather.getTemperature());
        preparedStatement.setInt(6, weather.getHumidity());
        preparedStatement.setString(7, weather.getDt().toString());

        return preparedStatement;
    }
}