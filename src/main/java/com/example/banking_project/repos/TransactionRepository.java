package com.example.banking_project.repos;

import com.example.banking_project.entities.Account;
import com.example.banking_project.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository <Transaction, Integer> {

    //ERROR IS ABOUT HERE.
    Optional<List<Transaction>> findByFromAccountOrToAccount(Account fromAccount, Account toAccount);
}
