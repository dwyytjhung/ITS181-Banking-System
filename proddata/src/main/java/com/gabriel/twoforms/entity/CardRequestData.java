package com.gabriel.twoforms.entity;

import com.gabriel.twoforms.models.CardRequest.Type;
import com.gabriel.twoforms.models.CardRequest.Status;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "card_request_data")
public class CardRequestData {
    @Id
    private String id;
    private String customerId;
    
    @Enumerated(EnumType.STRING)
    private Type type;
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    private LocalDate requestDate;
}
