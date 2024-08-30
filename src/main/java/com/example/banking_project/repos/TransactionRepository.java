package com.example.banking_project.repos;

import com.example.banking_project.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository <Transaction, Integer> {
}
