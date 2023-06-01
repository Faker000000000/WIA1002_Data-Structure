import com.example.tradingSystem.Buy;
import com.example.tradingSystem.User;
import com.example.tradingSystem.Order;
public class TestBuy {

    public static void main(String[] args) {
        
       
        // Create example users for testing purpose.
        User chin = new User("22004883", "Chin ");
        User yap = new User("22004749", "Yap");
        
       // Test addToBuyPendingOrder0Z
//        Buy.addToBuyPendingOrder("AAPL", 10, 1000.0, chin.getParticipantId());
//        Buy.addToBuyPendingOrder("MYT", 5, 50.0, ng.getParticipantId());
//        Buy.addToBuyPendingOrder("Huawei", 15, 35.0, yap.getParticipantId());
//        Buy.addToBuyPendingOrder("Android", 13, 45.60, tan.getParticipantId());
        
        // Test executeBuyOrder
        Buy.executeBuyOrder("22004749", "Huawei", 15, 35, yap.getInitialFunds());
//        System.out.println(chin.fetchCurrentFundsFromDatabase());;
       
        ;
    }

}
