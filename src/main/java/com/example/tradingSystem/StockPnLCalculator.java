package com.example.tradingSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockPnLCalculator {

    public static double calculatePnL(String participantId, int lots) {
        double buyingPricePerShare = fetchBuyingPriceFromDatabase(participantId);
        double sellingPricePerShare = fetchSellingPriceFromDatabase(participantId);
        int quantity = lots * 100; // Convert lots to shares
        double cost = buyingPricePerShare * quantity;
        double revenue = sellingPricePerShare * quantity;
        double pnl = revenue - cost;

        double originalPnL = fetchPnLFromDatabase(participantId); // Fetch the original P&L from the database
        double updatedPnL = originalPnL + pnl; // Update the P&L by adding the current P&L

        updatePnLInDatabase(participantId, updatedPnL);

        return pnl;
    }

    public static double fetchPnLFromDatabase(String participantId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish a database connection
            connection = getConnection();

            // Prepare the SQL statement to retrieve the P&L
            String sql = "SELECT PnL FROM user WHERE Id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, participantId);

            // Execute the query
            resultSet = statement.executeQuery();

            // Fetch the P&L from the result set
            if (resultSet.next()) {
                return resultSet.getDouble("PnL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the database resources
            closeDatabaseResources(connection, statement, resultSet);
        }

        return 0.0; // Return 0 if P&L not found or error occurred
    }

    public static double fetchBuyingPriceFromDatabase(String participantId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish a database connection
            connection = getConnection();

            // Prepare the SQL statement to retrieve the buying price
            String sql = "SELECT BuyPrice FROM buy WHERE Id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, participantId);

            // Execute the query
            resultSet = statement.executeQuery();

            // Fetch the buying price from the result set
            if (resultSet.next()) {
                return resultSet.getDouble("BuyPrice");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the database resources
            closeDatabaseResources(connection, statement, resultSet);
        }

        return 0.0; // Return 0 if buying price not found or error occurred
    }

    public static double fetchSellingPriceFromDatabase(String participantId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish a database connection
            connection = getConnection();

            // Prepare the SQL statement to retrieve the selling price
            String sql = "SELECT SellPrice FROM sell WHERE Id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, participantId);

            // Execute the query
            resultSet = statement.executeQuery();

            // Fetch the selling price from the result set
            if (resultSet.next()) {
                return resultSet.getDouble("SellPrice");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the database resources
            closeDatabaseResources(connection, statement, resultSet);
        }

        return 0.0; // Return 0 if selling price not found or error occurred
    }

    public static int fetchSellLotsFromDatabase(String participantId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish a database connection
            connection = getConnection();

            // Prepare the SQL statement to retrieve the quantity
            String sql = "SELECT Lots FROM sell WHERE Id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, participantId);

            // Execute the query
            resultSet = statement.executeQuery();

            // Fetch the quantity from the result set
            if (resultSet.next()) {
                return resultSet.getInt("Lots");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the database resources
            closeDatabaseResources(connection, statement, resultSet);
        }

        return 0; // Return 0 if quantity not found or error occurred
    }

    public static int fetchBuyLotsFromDatabase(String participantId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish a database connection
            connection = getConnection();

            // Prepare the SQL statement to retrieve the quantity
            String sql = "SELECT Lots FROM buy WHERE Id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, participantId);

            // Execute the query
            resultSet = statement.executeQuery();

            // Fetch the quantity from the result set
            if (resultSet.next()) {
                return resultSet.getInt("Lots");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the database resources
            closeDatabaseResources(connection, statement, resultSet);
        }

        return 0; // Return 0 if quantity not found or error occurred
    }

    public static void insertPnLIntoDatabase(String participantId, double pnl) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Establish a database connection
            connection = getConnection();

            // Prepare the SQL statement to insert P&L
            String sql = "INSERT INTO user (Id, PnL) VALUES (?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, participantId);
            statement.setDouble(2, pnl);

            // Execute the insert
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the database resources
            closeDatabaseResources(connection, statement, null);
        }
    }

    public static void updatePnLInDatabase(String participantId, double pnl) {
        try ( Connection connection = getConnection();  PreparedStatement statement = connection.prepareStatement("UPDATE user SET PnL = ? WHERE Id = ?")) {

            statement.setDouble(1, pnl);
            statement.setString(2, participantId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isRecordExists(String participantId) {
        boolean exists = false;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish a database connection
            connection = getConnection();

            // Prepare the SQL statement to check if the record exists
            String sql = "SELECT COUNT(*) FROM user WHERE Id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, participantId);

            // Execute the query
            resultSet = statement.executeQuery();

            // Check the result
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                exists = (count > 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the database resources
            closeDatabaseResources(connection, statement, resultSet);
        }

        return exists;
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "yap04270520");
    }

    private static void closeDatabaseResources(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
