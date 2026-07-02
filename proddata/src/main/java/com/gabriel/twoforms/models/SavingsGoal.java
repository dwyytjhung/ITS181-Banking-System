package com.gabriel.twoforms.models;

import java.time.LocalDate;

public class SavingsGoal {
    private String id;
    private String customerId;
    private String name;
    private double targetAmount;
    private double currentAmount;
    private String description;
    private LocalDate targetDate;
    private Status status;
    private boolean achievementUnlocked;

    public enum Status {
        ACTIVE, COMPLETED, PAUSED, CANCELLED
    }

    public SavingsGoal() {
    }

    public SavingsGoal(String id, String customerId, String name, double targetAmount, String description, LocalDate targetDate) {
        this.id = id;
        this.customerId = customerId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = 0.0;
        this.description = description;
        this.targetDate = targetDate;
        this.status = Status.ACTIVE;
        this.achievementUnlocked = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }

    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public boolean isAchievementUnlocked() { return achievementUnlocked; }
    public void setAchievementUnlocked(boolean achievementUnlocked) { this.achievementUnlocked = achievementUnlocked; }
}
