package com.example.tradingSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ScoringSystem {

    private static final double STARTING_ACCOUNT_BALANCE = 50000.0;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/demo";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "yap04270520";

    public static double calculatePoints(String participantId) {
        double profitAndLoss = StockPnLCalculator.calculatePnL(participantId, StockPnLCalculator.fetchSellLotsFromDatabase(participantId));
        double currentPoints = fetchPointsFromDatabase(participantId);
        double updatedPoints = currentPoints + ((profitAndLoss / STARTING_ACCOUNT_BALANCE) * 100);
        return updatedPoints;
    }

    public static void savePointsToDatabase(String participantId, double points) {
        if (isRecordExists(participantId)) {
            updatePointsInDatabase(participantId, points);
        } else {
            insertPointsIntoDatabase(participantId, points);
        }
    }

    public static boolean isRecordExists(String participantId) {
        try ( Connection connection = getConnection();  PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM user WHERE Id = ?")) {

            statement.setString(1, participantId);
            try ( ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void updatePointsInDatabase(String participantId, double points) {
        try ( Connection connection = getConnection();  PreparedStatement statement = connection.prepareStatement("UPDATE user SET Points = ? WHERE Id = ?")) {

            statement.setDouble(1, points);
            statement.setString(2, participantId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertPointsIntoDatabase(String participantId, double points) {
        try ( Connection connection = getConnection();  PreparedStatement statement = connection.prepareStatement("INSERT INTO user (Id, Points) VALUES (?, ?)")) {

            statement.setString(1, participantId);
            statement.setDouble(2, points);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    public static double fetchPointsFromDatabase(String participantId) {
        try ( Connection connection = getConnection();  PreparedStatement statement = connection.prepareStatement("SELECT Points FROM user WHERE Id = ?")) {

            statement.setString(1, participantId);
            try ( ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("Points");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Default value if participant ID is not found or an error occurs
    }
}
