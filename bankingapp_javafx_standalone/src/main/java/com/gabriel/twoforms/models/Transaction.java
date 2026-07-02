package com.gabriel.twoforms.models;

import java.time.LocalDateTime;

public class Transaction {
    private String id;
    private String accountId;
    private double amount;
    private Type type;
    private LocalDateTime timestamp;
    private String description;

    public enum Type {
        DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT, FEE
    }

    public Transaction(String id, String accountId, double amount, Type type, String description) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getAccountId() { return accountId; }
    public double getAmount() { return amount; }
    public Type getType() { return type; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getDescription() { return description; }
}
