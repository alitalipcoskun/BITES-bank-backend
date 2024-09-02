package com.example.banking_project.controllers;


import com.example.banking_project.dtos.AccountDTO;
import com.example.banking_project.dtos.TransactionDTO;
import com.example.banking_project.requests.*;
import com.example.banking_project.services.AccountService;
import com.example.banking_project.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final TransactionService transactionService;


    //Create
    @PostMapping("/new-account")
    public ResponseEntity<AccountDTO> createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ){
        return ResponseEntity.ok(accountService.create(request));
    }

    //Update
    @PostMapping("/add-balance")
    public ResponseEntity<AccountDTO> addBalance(
            @Valid @RequestBody AccAddBalanceReq request
    ){
        return ResponseEntity.ok(accountService.addBalance(request));
    }

    //Read
    @GetMapping("/accounts")
    public ResponseEntity <List<AccountDTO>> getAccount(
            @Valid @RequestBody GetAccountRequest request
    ){
        return ResponseEntity.ok(accountService.getAccounts(request));
    }

    // Delete account operation.
    // If the user has balance on its account, then it must return to the new ui to transfer the money to its another account.
    // Then If the user has no balance on its account, then deletion process can continue. This logic may be implemented through
    // frontend.
    @PostMapping("/delete-account")
    public ResponseEntity <List<AccountDTO>> deleteAccount(
            @Valid @RequestBody DelAccRequest request
    ){
        return ResponseEntity.ok(accountService.deleteAccount(request));
    }
}