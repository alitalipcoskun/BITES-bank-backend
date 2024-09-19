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


@CrossOrigin(origins = "http://localhost:3000")  // Allow your frontend URL
@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    @PostMapping("/transfer-money")
    public ResponseEntity<TransactionDTO> transferMoney(
            @Valid @RequestBody TransferMoneyReq request
    ) throws Exception {
        return ResponseEntity.ok(transactionService.makeTransaction(request));
    }
    @GetMapping("/list-transactions")
    public ResponseEntity<List<TransactionDTO>> listTransactions(
            @Valid @RequestParam("phoneNumber") String phoneNumber,
            @Valid @RequestParam("accountNo") String accountNo
    ){
        return ResponseEntity.ok(transactionService.listTransaction(ListTransactionsReq.builder()
                .accountNo(accountNo)
                .phoneNumber(phoneNumber)
                .build()));
    }
}
