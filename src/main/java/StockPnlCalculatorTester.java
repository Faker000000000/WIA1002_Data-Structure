
import com.example.tradingSystem.*;
import com.example.tradingSystem.TradingSystem;
import com.example.tradingSystem.StockPnLCalculator;
import com.example.tradingSystem.ScoringSystem;

public class StockPnlCalculatorTester {

    private final static String participantId = "22004749";

    public static void main(String[] args) {

        User user = User.fetchParticipantFromDatabase(participantId);

        if (user != null) {
            System.out.println("Participant ID: " + user.getId());
            System.out.println("Username: " + user.getName());
            System.out.println("Current Fund: " + user.getInitialFunds());
        } else {
            System.out.println("User not found or an error occurred.");
        }

        // Test case 1: Calculate P&L and update in database
        int lots = StockPnLCalculator.fetchSellLotsFromDatabase(participantId);
        System.out.println("Lots to be sold: " + StockPnLCalculator.fetchSellLotsFromDatabase(participantId));
        System.out.println("Selling price per share: " + StockPnLCalculator.fetchSellingPriceFromDatabase(participantId));
        System.out.println(StockPnLCalculator.fetchBuyLotsFromDatabase(participantId));
        System.out.println(StockPnLCalculator.fetchBuyingPriceFromDatabase(participantId));
        System.out.println(StockPnLCalculator.fetchPnLFromDatabase(participantId));

        double pnl = StockPnLCalculator.calculatePnL(participantId, lots);
        System.out.println("Calculated P&L: " + pnl);

        double updatedPnL = StockPnLCalculator.fetchPnLFromDatabase(participantId);
        System.out.println("Updated P&L in database: " + updatedPnL);
        
    }
}
