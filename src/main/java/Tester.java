
import com.example.tradingSystem.*;
import com.example.tradingSystem.TradingSystem;
import com.example.tradingSystem.StockPnLCalculator;
import com.example.tradingSystem.ScoringSystem;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.Duration;
import com.example.tradingSystem.*;

import com.example.tradingSystem.*;

public class Tester {

    private final static String participantId = "22004749";

    public static void main(String[] args) {

        // Test the User code
        System.out.println("User information: ");
        User user = User.fetchParticipantFromDatabase(participantId);

        if (user != null) {
            System.out.println("Participant ID: " + user.getId());
            System.out.println("Username: " + user.getName());
            System.out.println("Current Fund: " + user.getInitialFunds());
        } else {
            System.out.println("User not found or an error occurred.");
        }
        System.out.println("");

        // Test the StockPnLCalculator code
        System.out.println("Calculate the PnL of the user: ");
        int lots = StockPnLCalculator.fetchSellLotsFromDatabase(participantId); // Fetch the number of lots being sold from the database
        double originalPnL = StockPnLCalculator.fetchPnLFromDatabase(participantId); // Fetch the original P&L from the database
        double pnl = StockPnLCalculator.calculatePnL(participantId, lots);
        double updatedPnL = originalPnL + pnl;

        System.out.println("Original P&L for Participant ID " + participantId + ": " + originalPnL);
        System.out.println("P&L for Participant ID " + participantId + ": " + pnl);

        if (StockPnLCalculator.isRecordExists(participantId)) {
            StockPnLCalculator.updatePnLInDatabase(participantId, updatedPnL); // Update P&L in the database
            System.out.println("Updated P&L for Participant ID " + participantId + ": " + updatedPnL);
        } else {
            StockPnLCalculator.insertPnLIntoDatabase(participantId, pnl); // Insert P&L into the database
            System.out.println("Inserted P&L for Participant ID " + participantId + ": " + pnl);
        }
        System.out.println("");
        
        // Test the ScoringSystem code
        System.out.println("Calculate the Points for the user: ");
        double points = ScoringSystem.calculatePoints(participantId);
        System.out.println("Points for Participant ID " + participantId + ": " + points);
        if (ScoringSystem.isRecordExists(participantId)) {
            ScoringSystem.updatePointsInDatabase(participantId, points); // Update points in the database
        } else {
            ScoringSystem.insertPointsIntoDatabase(participantId, points); // Insert points into the database
        }
        System.out.println("");

//        // Create a new trading system.
//        TradingSystem tradingSystem = new TradingSystem(LocalDateTime.now());
//        // Creating and placing some buy orders
//        Order buyOrder1 = new Order("AAPL", 150.0, 100);
//        tradingSystem.placeBuyOrder(buyOrder1);
//
//        Order buyOrder2 = new Order("GOOGL", 2500.0, 200);
//        tradingSystem.placeBuyOrder(buyOrder2);
//
//        // Creating and placing some sell orders
//        Order sellOrder1 = new Order("AAPL", 160.0, 50);
//        tradingSystem.placeSellOrder(sellOrder1);
//
//        Order sellOrder2 = new Order("GOOGL", 2600.0, 300);
//        tradingSystem.placeSellOrder(sellOrder2);

    }

}
