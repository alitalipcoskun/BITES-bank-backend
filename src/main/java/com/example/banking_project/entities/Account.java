package com.example.banking_project.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data // Data creates automatically Getters and Setters for the attributes of the class.
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="\"account\"")
@Accessors(chain=true)
public class Account implements Serializable {
    @Serial
    private static final long serialVersionUID = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;

    //Entity class to create account table and use it.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String no;
    private float balance;
    private String moneyType;

    @OneToMany(mappedBy = "fromAccount")
    private List<Transaction> sentTransactions;

    @OneToMany(mappedBy = "toAccount")
    private List<Transaction> receivedTransactions;
}
