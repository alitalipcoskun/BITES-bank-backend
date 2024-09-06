package com.example.banking_project.services;


import com.example.banking_project.config.JwtService;
import com.example.banking_project.dtos.AccountDTO;
import com.example.banking_project.dtos.TransactionDTO;
import com.example.banking_project.entities.Account;
import com.example.banking_project.entities.Transaction;
import com.example.banking_project.repos.AccountRepository;
import com.example.banking_project.repos.TransactionRepository;
import com.example.banking_project.repos.UserRepository;
import com.example.banking_project.requests.ListTransactionsReq;
import com.example.banking_project.requests.TransferMoneyReq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final JwtService jwtService;
    private final AccountService accountService;

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionDTO makeTransaction(TransferMoneyReq request){
        //Extracting information from received request
        String fromAccNo = request.getFrom_account();
        String toAccNo = request.getTo_account();

        //Retrieve from_acc owner
        Optional<Account> fromAccOpt = accountRepository.findByNo(fromAccNo);
        //Extract account
        Account fromAcc = extractData(fromAccOpt);
        //LUHN algoritması
        //Extract the phone number of current session
        String sessionPhoneNum = extractPhone();

        // Verify that session has authorization to send money from given account.
        validateAccOwner(fromAcc, sessionPhoneNum);

        //Extract transaction amount from request
        Float transactionAmount = request.getBalanceChange();

        // Verify that account has sufficient amount of balance to send money.
        validateAccBalance(fromAcc, transactionAmount);

        // Send money
            //Find toAccount
            Optional<Account> toAccOpt = accountRepository.findByNo(toAccNo);
            Account toAcc = extractData(toAccOpt);

            //(Type check for the accounts may be implemented to here...)

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

        return TransactionDTO.builder()
                .id(newTransaction.getId())
                .fromAccount(fromAcc.getNo())
                .toAccount(toAcc.getNo())
                .transferAmount(transactionAmount)
                .date(String.valueOf(new Date(System.currentTimeMillis())))
                .build();
    }

    private <T> T extractData(Optional<T> optionalT){
        //Checking whether user has optional data or not.
        if(optionalT.isEmpty()){
            throw new IllegalArgumentException("Return type is empty.");
        }
        return optionalT.get();
    }

    private String extractPhone(){
        //User JWT is provided by Bearer part.
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Returns response entity UNAUTHORIZED for the user that does not have JWT.
        if (userDetails == null) {
            throw new IllegalArgumentException("User not found!");
        }
        //Extracting subject
        return userDetails.getUsername();
    }
    private void validateAccOwner(Account account, String userPhone){
        if(!Objects.equals(account.getUser().getPhone(), userPhone)){
            throw new IllegalArgumentException("User does not have permission to delete this account.");
        }
    }
    private void validateAccBalance(Account account, Float validationAmount){
        if(account.getBalance() < validationAmount){
            throw new IllegalArgumentException("Account has balance. Try to transfer it to the different account.");
        }
    }

    public List<TransactionDTO> listTransaction(ListTransactionsReq request) {
        //Extract accountNo and userPhone from user
        String accountNo = request.getAccountNo();
        String reqPhone = extractPhone();

        String sessionPhone = extractPhone();
        //Extract account
        Optional<Account> accOpt = accountRepository.findByNo(accountNo);
        Account account = extractData(accOpt);
        validateAccOwner(account,reqPhone);

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
