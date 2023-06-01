package com.example.tradingSystem;

public class Order {

    private static int nextId = 1;

    private User user;
    private String id;
    private String symbol;
    private double price;
    private int lots;

    public Order(User user, String symbol, double price, int lots) {
        this.user.getId().equals(id);
        this.symbol = symbol;
        this.price = price;
        this.lots = lots;
    }

    public String getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public int getLots() {
        return lots;
    }

    public void setLots(int lots) {
        this.lots = lots;
    }
}
