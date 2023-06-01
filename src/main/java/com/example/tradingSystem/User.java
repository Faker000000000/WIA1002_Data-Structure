package com.example.tradingSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class User {

    private String id;
    private String name;
    private double initialFunds;

    private Map<String, Integer> holdings; // Map to store stock symbol and corresponding number of shares

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.initialFunds = 50000;
        this.holdings = new HashMap<>();
    }

    // Method to fetch participant information from the database
  public static User fetchParticipantFromDatabase(String participantId) {
    try (Connection connection = getConnection();
         PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE Id = ?")) {
        statement.setString(1, participantId);
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                String id = resultSet.getString("Id");
                double currentFund = resultSet.getDouble("CurrentFund");
                String username = resultSet.getString("Username");
                return new User(id, username);
            } else {
                System.out.println("Participant not found in the database.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error executing the SQL query: " + e.getMessage());
    }
    return null;
}

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "yap04270520");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getInitialFunds() {
        return initialFunds;
    }

    public void setInitialFunds(double initialFunds) {
        this.initialFunds = initialFunds;
    }

 
    public boolean checkAccountBalanceByTime(LocalTime currentTime) {
        LocalTime openingTime = LocalTime.of(9, 0); // 9:00 AM
        LocalTime closingTime = LocalTime.of(17, 0); // 5:00 PM

        if (currentTime.isBefore(openingTime) || currentTime.isAfter(closingTime)) {
            // Outside trading hours, no need to check the balance
            return true;
        }

        if (currentTime.equals(openingTime)) {
            double startingBalance = fetchCurrentFundsFromDatabase();
            if (startingBalance == 0.0) {
                System.out.println("Unable to fetch the starting balance from the database.");
                return false;
            }
            initialFunds = startingBalance;
        }

        double accountBalanceThreshold = getCurrentDayStartingBalance() * 0.5;
        if (initialFunds >= accountBalanceThreshold) {
            System.out.println("Account balance exceeds 50% of the starting balance.");
            return false;
        }

        return true;
    }

    public double fetchCurrentFundsFromDatabase() {
        // Fetch the current funds for the user from the database at 9 AM
        try ( Connection connection = getConnection();  PreparedStatement statement = connection.prepareStatement("SELECT CurrentFund FROM user WHERE Id = ?")) {
            statement.setString(1, id);
            try ( ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("CurrentFund");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Return 0 if current funds not found or error occurred
    }

    private double getCurrentDayStartingBalance() {
        LocalDate currentDate = LocalDate.now();
        DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek();

        // Check if it's a weekend (Saturday or Sunday)
        if (currentDayOfWeek == DayOfWeek.SATURDAY || currentDayOfWeek == DayOfWeek.SUNDAY) {
            return 0.0; // No trading on weekends, so return 0 as the starting balance
        }

        // Fetch the starting balance for the current day from the database
        double startingBalance = fetchStartingBalanceFromDatabase(currentDate);
        if (startingBalance == 0.0) {
            System.out.println("Unable to fetch the starting balance from the database.");
            return 0.0; // Return 0 if starting balance not found or error occurred
        }

        return startingBalance;
    }

    private double fetchStartingBalanceFromDatabase(LocalDate currentDate) {
        // Fetch the starting balance for the current day from the database
        try ( Connection connection = getConnection();  PreparedStatement statement = connection.prepareStatement("SELECT StartingBalance FROM daily_balance WHERE Date = ?")) {
            statement.setDate(1, java.sql.Date.valueOf(currentDate));
            try ( ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("StartingBalance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Return 0 if starting balance not found or error occurred
    }

    public boolean checkTradingRestrictions() {
        double startingAccountBalance = 50000.0; // Initial funds: RM50,000/account
        double accountBalanceThreshold = startingAccountBalance * 0.5;
        if (initialFunds >= accountBalanceThreshold) {
            System.out.println("Account balance exceeds 50% of the starting balance.");
            return false;
        }

        // Implement other trading restrictions here, such as margin trading and short selling
        // Add the necessary logic based on the requirements mentioned
        return true; // Trading restrictions check passed
    }

    public Map<String, Integer> getHoldings() {
        return holdings;
    }

 

    public void displayHoldings() {
        System.out.println("Participant Holdings:");
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            String stockSymbol = entry.getKey();
            int quantity = entry.getValue();
            System.out.println(stockSymbol + ": " + quantity);
        }
    }
}
