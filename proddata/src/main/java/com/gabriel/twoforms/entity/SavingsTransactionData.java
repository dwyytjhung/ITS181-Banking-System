package com.gabriel.twoforms.entity;

import com.gabriel.twoforms.models.SavingsTransaction.Type;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "savings_transaction_data")
public class SavingsTransactionData {
    @Id
    private String id;
    private String goalId;
    private double amount;
    
    @Enumerated(EnumType.STRING)
    private Type type;
    
    private LocalDateTime timestamp;
    private double updatedBalance;
}
