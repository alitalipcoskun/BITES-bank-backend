package com.example.banking_project.controllers;


import com.example.banking_project.dtos.AccountDTO;
import com.example.banking_project.requests.*;
import com.example.banking_project.responses.AccOwnerResponse;
import com.example.banking_project.services.AccountService;
import com.example.banking_project.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")  // Allow your frontend URL
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    //Create
    @PostMapping("/new-account")
    public ResponseEntity<AccountDTO> createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ){
        // Perform account creation in AccountService.
        return ResponseEntity.ok(accountService.create(request));
    }

    @GetMapping("/search-account-owner")
    public ResponseEntity<AccOwnerResponse> accountOwner(
            @RequestParam("no") String accountNo
    ){

        return ResponseEntity.ok(accountService.searchOwner(accountNo));
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
            @RequestParam("phoneNumber") String phoneNumber
    ){
        return ResponseEntity.ok(accountService.getAccounts(phoneNumber));
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

    @GetMapping("/account-owner")
    public ResponseEntity<String> getAccountOwner(
            @RequestParam("accountNo") String accountNo
    ){
        return ResponseEntity.ok(accountService.getAccountOwner(accountNo));
    }

}