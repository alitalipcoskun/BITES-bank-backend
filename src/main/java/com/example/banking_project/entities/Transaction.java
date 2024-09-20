package com.example.banking_project.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;


// Data creates automatically Getters and Setters for the attributes of the class.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="\"transaction\"")
@Accessors(chain=true)
public class Transaction implements Serializable {
    @Serial
    private static final long serialVersionUID = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    //Entity class for transaction table creation and processes.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="fromAccount", referencedColumnName = "id")
    private Account fromAccount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="toAccount", referencedColumnName = "id")
    private Account toAccount;
    private Float transferAmount;
    @CreationTimestamp
    private String date;
}
