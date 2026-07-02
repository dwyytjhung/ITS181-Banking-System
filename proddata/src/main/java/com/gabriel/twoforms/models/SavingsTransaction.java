package com.gabriel.twoforms.models;

import java.time.LocalDateTime;

public class SavingsTransaction {
    private String id;
    private String goalId;
    private double amount;
    private Type type;
    private LocalDateTime timestamp;
    private double updatedBalance;

    public enum Type {
        DEPOSIT, WITHDRAWAL
    }

    public SavingsTransaction() {
    }

    public SavingsTransaction(String id, String goalId, double amount, Type type, double updatedBalance) {
        this.id = id;
        this.goalId = goalId;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.updatedBalance = updatedBalance;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getGoalId() { return goalId; }
    public void setGoalId(String goalId) { this.goalId = goalId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public double getUpdatedBalance() { return updatedBalance; }
    public void setUpdatedBalance(double updatedBalance) { this.updatedBalance = updatedBalance; }
}
