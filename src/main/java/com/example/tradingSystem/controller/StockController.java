
package com.example.tradingSystem.controller;


import com.example.tradingSystem.TradingSystem;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock")
public class StockController {
    private TradingSystem tradingSystem;

    public StockController() {
        this.tradingSystem = new TradingSystem();
    }

    @PostMapping("/buy")
    public String buyShares() {
        // Check if it is within the initial trading period
        if (tradingSystem.isWithinInitialTradingPeriod()) {
            // Check if it is within the trading hours
            if (tradingSystem.isWithinTradingHours()) {
                return "Purchase Success!";
            } else {
                return "Trading hours have ended.";
            }
        } else {
            return "Cannot purchase shares. Initial trading period has ended.";
        }
    }
}