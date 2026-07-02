package com.gabriel.twoforms.entity;

import com.gabriel.twoforms.models.Notification.Type;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notification_data")
public class NotificationData {
    @Id
    private String id;
    private String recipientId;
    private String title;
    private String message;
    
    @Enumerated(EnumType.STRING)
    private Type type;
    
    @Column(name = "is_read")
    private boolean read;
    
    private LocalDateTime timestamp;
}
