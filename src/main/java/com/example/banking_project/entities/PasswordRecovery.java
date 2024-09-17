package com.example.banking_project.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import jakarta.persistence.Id;

@Data // Data creates automatically Getters and Setters for the attributes of the class.
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("PasswordRecovery")
public class PasswordRecovery {
    @Id
    private String id;
    private String mail;
    private String token;
    private String creationDate;
    private String expirationDate;

}
 