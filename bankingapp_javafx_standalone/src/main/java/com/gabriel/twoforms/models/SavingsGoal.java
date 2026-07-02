package com.gabriel.twoforms.models;

public class SavingsGoal {
    private String id;
    private String customerId;
    private String name;
    private double targetAmount;
    private double currentAmount;

    public SavingsGoal(String id, String customerId, String name, double targetAmount) {
        this.id = id;
        this.customerId = customerId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = 0.0;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public double getTargetAmount() { return targetAmount; }
    public double getCurrentAmount() { return currentAmount; }

    public void addFunds(double amount) {
        if (amount > 0) {
            this.currentAmount += amount;
        }
    }
}
