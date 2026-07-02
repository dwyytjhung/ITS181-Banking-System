package com.gabriel.twoforms.entity;

import com.gabriel.twoforms.models.SavingsGoal.Status;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "savings_goal_data")
public class SavingsGoalData {
    @Id
    private String id;
    private String customerId;
    private String name;
    private double targetAmount;
    private double currentAmount;
    private String description;
    private LocalDate targetDate;
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    private boolean achievementUnlocked;
}
