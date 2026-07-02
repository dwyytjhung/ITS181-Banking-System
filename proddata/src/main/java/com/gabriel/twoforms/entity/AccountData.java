package com.gabriel.twoforms.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "account_data")
public class AccountData {
    @Id
    private String accountNumber;
    private String customerId;
    private double balance;
}
