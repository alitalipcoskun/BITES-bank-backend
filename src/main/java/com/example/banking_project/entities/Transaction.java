package com.example.banking_project.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Data creates automatically Getters and Setters for the attributes of the class.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="\"transaction\"")
public class Transaction {

    //Entity class for transaction table creation and processes.


    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    @JoinColumn(name="fromAccount", referencedColumnName = "id")
    private Account fromAccount;
    @ManyToOne
    @JoinColumn(name="toAccount", referencedColumnName = "id")
    private Account toAccount;
    private Float transferAmount;
    private String date;
}
