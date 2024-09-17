package com.example.banking_project.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data // Data creates automatically Getters and Setters for the attributes of the class.
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="\"account\"")
@Accessors(chain=true)
public class Account {

    //Entity class to create account table and use it.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String no;
    private float balance;
    private String money_type;

    @OneToMany(mappedBy = "fromAccount")
    private List<Transaction> sentTransactions;

    @OneToMany(mappedBy = "toAccount")
    private List<Transaction> receivedTransactions;
}
