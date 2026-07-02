package com.gabriel.twoforms.entity;

import com.gabriel.twoforms.models.Transaction.Type;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transaction_data")
public class TransactionData {
    @Id
    private String id;
    private String accountId;
    private double amount;
    
    @Enumerated(EnumType.STRING)
    private Type type;
    
    private LocalDateTime timestamp;
    private String description;
}
