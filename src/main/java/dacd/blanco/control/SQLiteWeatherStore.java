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
    private Connection connection;

    public SQLiteWeatherStore(String dbPath) {
        try {
            connection = connect(dbPath);
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to SQLite database", e);
        }
    }

    private void createTables() {
        try (Statement statement = connection.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS weather_data (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "location_name TEXT," +
                    "latitude DOUBLE," +
                    "longitude DOUBLE," +
                    "datetime TIMESTAMP," +
                    "temperature DOUBLE," +
                    "humidity INTEGER," +
                    "clouds INTEGER," +
                    "wind_speed DOUBLE," +
                    "rain_probability DOUBLE" +
                    ")";
            statement.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating tables", e);
        }
    }

    @Override
    public void saveWeather(Location location, Instant dt, Weather weather) {
        try {
            String insertSQL = "INSERT INTO weather_data (location_name, latitude, longitude, datetime, temperature, humidity, clouds, wind_speed, rain_probability) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setString(1, location.getName());
                preparedStatement.setDouble(2, location.getLatitude());
                preparedStatement.setDouble(3, location.getLongitude());
                preparedStatement.setTimestamp(4, Timestamp.from(dt));
                preparedStatement.setDouble(5, weather.getTemperature());
                preparedStatement.setInt(6, weather.getHumidity());
                preparedStatement.setInt(7, weather.getClouds());
                preparedStatement.setDouble(8, weather.getWindSpeed());
                preparedStatement.setDouble(9, weather.getRainProb());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving weather data", e);
        }
    }

    @Override
    public Weather loadWeather(Location location, Instant dt) {
        try {
            String selectSQL = "SELECT * FROM weather_data WHERE location_name = ? AND datetime = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
                preparedStatement.setString(1, location.getName());
                preparedStatement.setTimestamp(2, Timestamp.from(dt));
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return new Weather(
                                resultSet.getInt("clouds"),
                                resultSet.getDouble("wind_speed"),
                                resultSet.getDouble("rain_probability"),
                                resultSet.getDouble("temperature"),
                                resultSet.getInt("humidity"),
                                resultSet.getTimestamp("datetime").toInstant()
                        );
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error loading weather data", e);
        }
    }

    private Connection connect(String dbPath) throws SQLException {
        Connection conn;
        String url = "jdbc:sqlite:" + dbPath;
        conn = DriverManager.getConnection(url);
        System.out.println("Connection to SQLite has been established.");
        return conn;
    }
}