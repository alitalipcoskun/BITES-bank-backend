package com.example.banking_project.controllers;


import com.example.banking_project.dtos.TransactionDTO;
import com.example.banking_project.requests.ListTransactionsReq;
import com.example.banking_project.requests.TransferMoneyReq;
import com.example.banking_project.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    @PostMapping("/transfer-money")
    public ResponseEntity<TransactionDTO> transferMoney(
            @Valid @RequestBody TransferMoneyReq request
    ){
        return ResponseEntity.ok(transactionService.makeTransaction(request));
    }
    @GetMapping("/list-transactions")
    public ResponseEntity<List<TransactionDTO>> listTransactions(
            @Valid @RequestBody ListTransactionsReq request
    ){
        return ResponseEntity.ok(transactionService.listTransaction(request));
    }
}
