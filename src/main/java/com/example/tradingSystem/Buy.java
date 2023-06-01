package com.example.tradingSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.UUID;

public class Buy {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/demo";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "yap04270520";

    public static void addToBuyPendingOrder(String symbol, int lots, double buyPrice, String id) {
        LocalDateTime orderPendingTime = LocalDateTime.now();

        try ( Connection connection = getConnection();  PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO buypendingorder (UID, Symbol, Lots, BuyPrice, OrderPendingTime) VALUES (?, ?, ?, ?, ?)")) {

            statement.setString(1, id);
            statement.setString(2, symbol);
            statement.setInt(3, lots);
            statement.setDouble(4, buyPrice);
            statement.setString(5, orderPendingTime.toString());
            statement.executeUpdate();

            System.out.println("Buy pending order added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void executeBuyOrder(String id, String symbol, int lots, double buyPrice, double currentFund) {
        double marketPrice = 35.1;  //  will be changed with the getMethod by Kahchun
        double acceptableRange = marketPrice * 0.01; // To calculate the accepted range
        double lowerLimit = marketPrice - acceptableRange; 
        double upperLimit = marketPrice + acceptableRange;

        if (buyPrice >= lowerLimit && buyPrice <= upperLimit) {
            double totalCost = buyPrice * lots;

            if (currentFund >= totalCost) {
                double updatedFund = currentFund - totalCost;
                updateCurrentFundInDatabase(id, updatedFund);

                int orderId = extractOrderIdFromPendingOrder(id, symbol, lots, buyPrice); // Pass user ID as well
                if (orderId != -1) {
                    addToBuyTable(orderId);
                    removeFromPendingOrderTable(orderId); // Remove the executed order from the pending order table
                    System.out.println("Buy order executed successfully.");
                } else {
                    System.out.println("Failed to extract order ID from the pending order.");
                }
            } else {
                System.out.println("Insufficient funds to execute the buy order.");
            }
        } else {
            System.out.println("Buy price is not within the acceptable range.");
        }
    }

    private static int extractOrderIdFromPendingOrder(String id, String symbol, int lots, double buyPrice) {
        try ( Connection connection = getConnection();  PreparedStatement selectStatement = connection.prepareStatement("SELECT OrderId FROM buypendingorder WHERE UID = ? AND Symbol = ? AND Lots = ? AND BuyPrice = ?")) {

            selectStatement.setString(1, id);
            selectStatement.setString(2, symbol);
            selectStatement.setInt(3, lots);
            selectStatement.setDouble(4, buyPrice);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("OrderId");
            } else {
                return -1; // Indicates failure to extract order ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Indicates failure to extract order ID
        }
    }

    private static void removeFromPendingOrderTable(int orderId) {
        try ( Connection connection = getConnection();  PreparedStatement statement = connection.prepareStatement("DELETE FROM buypendingorder WHERE OrderId = ?")) {

            statement.setInt(1, orderId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Executed order removed from the pending order table successfully.");
            } else {
                System.out.println("No pending order found with the specified order ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateCurrentFundInDatabase(String id, double updatedFund) {
        try ( Connection connection = getConnection();  PreparedStatement statement = connection.prepareStatement("UPDATE user SET CurrentFund = ? WHERE Id = ?")) {

            statement.setDouble(1, updatedFund);
            statement.setString(2, id);
            statement.executeUpdate();

            System.out.println("Current fund updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addToBuyTable(int orderId) {
        try ( Connection connection = getConnection();  PreparedStatement selectStatement = connection.prepareStatement("SELECT OrderId, UID, Symbol, BuyPrice, Lots FROM buypendingorder WHERE OrderId = ?");  PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO buy (OrderId, Id, Symbol, BuyPrice, Lots) VALUES (?, ?, ?, ?, ?)")) {

            selectStatement.setInt(1, orderId);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                int selectedOrderId = resultSet.getInt("OrderId");
                String id = resultSet.getString("UID");
                String symbol = resultSet.getString("Symbol");
                double buyPrice = resultSet.getDouble("BuyPrice");
                int lots = resultSet.getInt("Lots");

                // Add the specific executed pending order to the buy table
                insertStatement.setInt(1, selectedOrderId);
                insertStatement.setString(2, id);
                insertStatement.setString(3, symbol);
                insertStatement.setDouble(4, buyPrice);
                insertStatement.setInt(5, lots);
                insertStatement.executeUpdate();

                System.out.println("Executed pending order added to the buy table successfully.");
            } else {
                System.out.println("Specified pending order not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }
}
