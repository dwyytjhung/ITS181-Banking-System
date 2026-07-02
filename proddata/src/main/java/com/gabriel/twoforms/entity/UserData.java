package com.gabriel.twoforms.entity;

import com.gabriel.twoforms.models.User.Role;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_data")
public class UserData {
    @Id
    private String id;
    private String username;
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    private String fullName;
    private String email;
    private String phone;
}
