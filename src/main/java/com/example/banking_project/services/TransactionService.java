package com.example.banking_project.services;


import com.example.banking_project.dtos.TransactionDTO;
import com.example.banking_project.entities.Account;
import com.example.banking_project.entities.Transaction;
import com.example.banking_project.exceptions.ResourceNotFoundException;
import com.example.banking_project.repos.AccountRepository;
import com.example.banking_project.repos.TransactionRepository;
import com.example.banking_project.requests.ListTransactionsReq;
import com.example.banking_project.requests.TransferMoneyReq;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionDTO makeTransaction( @Valid @RequestBody TransferMoneyReq request){
        //Extracting information from received request
        String fromAccNo = request.getFromAcc();
        String toAccNo = request.getToAcc();

        //Retrieve from_acc owner
        Account fromAcc = findAccountByNo(fromAccNo);
        //Extract the phone number of current session
        String sessionPhoneNum = extractPhone();

        // Verify that session has authorization to send money from given account.
        validateAccOwner(fromAcc, sessionPhoneNum);

        //Extract transaction amount from request
        Float transactionAmount = request.getAmount();

        // Verify that account has sufficient amount of balance to send money.
        validateAccBalance(fromAcc, transactionAmount);

        // Send money
            //Find toAccount
            Account toAcc = findAccountByNo(toAccNo);

            //(Type check for the accounts may be implemented to here...)

                log.info("Account tries to transfer");

                // Add recieved_account balance to the recieved_account balance
                fromAcc.setBalance(fromAcc.getBalance() - transactionAmount);
                // Save to the repo
                accountRepository.save(fromAcc);
                // Substract the from_account balance to the amount
                toAcc.setBalance(toAcc.getBalance() + transactionAmount);
                // Save to the repo
                accountRepository.save(toAcc);

        // Create transaction
        var newTransaction = Transaction.builder()
                .fromAccount(fromAcc)
                .toAccount(toAcc)
                .transferAmount(transactionAmount)
                .build();

        transactionRepository.save(newTransaction);
        // Return TransactionDTO

        log.info(String.valueOf(newTransaction.getTransferAmount()));

        return TransactionDTO.builder()
                .id(newTransaction.getId())
                .fromAccount(fromAcc.getNo())
                .toAccount(toAcc.getNo())
                .transferAmount(transactionAmount)
                .date(String.valueOf(new Date(System.currentTimeMillis())))
                .build();
    }

    private Account findAccountByNo(String accNo){
        log.info("Account searching..");
        return accountRepository.findByNo(accNo)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    private <T> T extractData(Optional<T> optionalT){
        //Checking whether user has optional data or not.
        if(optionalT.isEmpty()){
            log.error("Null returned!");
            throw new IllegalArgumentException("Return type is empty.");
        }
        return optionalT.get();
    }

    private String extractPhone(){
        //User JWT is provided by Bearer part.
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Returns response entity UNAUTHORIZED for the user that does not have JWT.
        if (userDetails == null) {
            log.error("Unexpected Bearer");
            throw new IllegalArgumentException("The session does not found!");
        }
        //Extracting subject
        return userDetails.getUsername();
    }
    private void validateAccOwner(Account account, String userPhone){
        if(!Objects.equals(account.getUser().getPhone(), userPhone)){
            log.error("Insufficient permission.");
            throw new IllegalArgumentException("User does not have permission to delete this account.");
        }
    }
    private void validateAccBalance(Account account, Float validationAmount){
        if(account.getBalance() < validationAmount){
            log.error("Insufficient balance.");
            throw new IllegalArgumentException("Account has insufficient balance.");
        }
    }

    public List<TransactionDTO> listTransaction(@Valid @RequestBody ListTransactionsReq request) {
        //Extract accountNo and userPhone from user
        String accountNo = request.getAccountNo();
        String sessionPhone = extractPhone();

        //Extract account
        Account account = findAccountByNo(accountNo);
        validateAccOwner(account,sessionPhone);

        Optional<List<Transaction>> transactionsOpt = transactionRepository.findByFromAccountOrToAccount(account, account);
        List<Transaction> transactions = extractData(transactionsOpt);

        //Returns the list of AccountResponse DTOs
        return transactions.stream().map(transaction -> new TransactionDTO(
                transaction.getId(),
                transaction.getFromAccount().getNo(),
                transaction.getToAccount().getNo(),
                transaction.getDate(),
                transaction.getTransferAmount()
        )).collect(Collectors.toList());

    }


}
