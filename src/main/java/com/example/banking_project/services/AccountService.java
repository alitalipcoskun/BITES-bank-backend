package com.example.banking_project.services;

import com.example.banking_project.config.JwtService;
import com.example.banking_project.entities.Account;
import com.example.banking_project.entities.User;
import com.example.banking_project.repos.AccountRepository;
import com.example.banking_project.repos.UserRepository;
import com.example.banking_project.requests.AccAddBalanceReq;
import com.example.banking_project.requests.CreateAccountRequest;
import com.example.banking_project.requests.DelAccRequest;
import com.example.banking_project.requests.GetAccountRequest;
import com.example.banking_project.dtos.AccountDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AccountService {
    private static final Random ACC_NO_GEN = new Random();
    private static final Integer ACC_GEN_BOUNDARY = 1_000_000_000;

    private final JwtService jwtService;

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public AccountDTO create(@Valid CreateAccountRequest request) {
        // Extract the user phone number from the token
        String userPhone = extractPhone();
        // Fetch the user based on the phone number
        Optional<User> userOpt = userRepository.findByPhone(userPhone);

        //Extracting user information
        User user = extractData(userOpt);

        // Build the new account associated with the fetched user
        var newAccount = Account.builder()
                .no(generateAccountNo())
                .user(user) // Set the user
                .balance(0)
                .money_type(request.getMoney_type())
                .build();

        // Save the new account to the database
        accountRepository.save(newAccount);

        // Build the response
        return AccountDTO.builder()
                .id(newAccount.getId())
                .balance(newAccount.getBalance())
                .money_type(newAccount.getMoney_type())
                .no(newAccount.getNo())
                .userId(user.getId())
                .build();
    }

    public List<AccountDTO> getAccounts(@Valid GetAccountRequest request){
        //BURAYI DANIŞ
        //Extracting unique data from request
        String userPhone = request.getPhoneNumber();
        //Validate that current user has permission to see the accounts
        String validationPhone = extractPhone();

        if(!Objects.equals(validationPhone, userPhone)){
            throw new IllegalArgumentException("No permission!");
        }

        //Finding user with specified unique value.
        Optional<User> userOpt = userRepository.findByPhone(userPhone);

        //Extracting from Optional Data Type
        User user = extractData(userOpt);
        //Finding accounts that belongs to the user.
        Optional<List<Account>> accountsOpt = accountRepository.findByUserId(user.getId());

        //Extracting account or accounts.
        List <Account> accounts = extractData(accountsOpt);

        //Returns the list of AccountResponse DTOs
        return accounts.stream().map(account -> new AccountDTO(
                account.getId(),
                account.getNo(),
                account.getBalance(),
                account.getMoney_type(),
                account.getUser().getId()
        )).collect(Collectors.toList());
    }
    private <T> T extractData(Optional<T> optionalT){
        //Checking whether user has optional data or not.
        if(optionalT.isEmpty()){
            throw new IllegalArgumentException("Return type is empty.");
        }
        return optionalT.get();
    }

    private static String generateAccountNo(){
        //Debug can be added after fetching all account numbers
        //with an if check
        return String.format("%010d", ACC_NO_GEN.nextInt(ACC_GEN_BOUNDARY));
    }


    public AccountDTO addBalance(AccAddBalanceReq request) {
        //Fetching the account data with the unique identifier
        Optional<Account> accountOpt = accountRepository.findByNo(request.getAccountNo());
        //Extracting data from Optional dtype
        Account account =  extractData(accountOpt);
        //Setting new balance to the account
        account.setBalance(account.getBalance()+request.getBalanceChange());
        accountRepository.save(account);

        Optional<Account> returnedAccountOpt = accountRepository.findByNo(request.getAccountNo());
        Account returnedAccount =  extractData(returnedAccountOpt);

        return AccountDTO.builder()
                .userId(returnedAccount.getUser().getId())
                .balance(returnedAccount.getBalance())
                .id(returnedAccount.getId())
                .money_type(returnedAccount.getMoney_type())
                .no(returnedAccount.getNo())
                .build();
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
        if(account.getBalance() > validationAmount){
            throw new IllegalArgumentException("Account has balance. Try to transfer it to the different account.");
        }
    }


    public List<AccountDTO> deleteAccount(DelAccRequest request) {
        //BURAYI DANIŞ.
        //Extracting unique user claim
        String userPhone = extractPhone();

        Optional<Account> accOpt = accountRepository.findByNo(request.getNo());
        Account account = extractData(accOpt);

        //Verify that account is owned by userPhone
        validateAccOwner(account, userPhone);

        //Verify that Account balance is equal to 0
        validateAccBalance(account, (float)0);

        //Remove from db
        accountRepository.removeByNo(account.getNo());

        //Extracting the account whose owned by current session user.
        Optional<List<Account>> accListOpt = accountRepository.findByUserId(account.getUser().getId());
        List<Account> accounts = extractData(accListOpt);

        //Return the accounts (if they exists)
        return accounts.stream().map(accountIter -> new AccountDTO(
                accountIter.getId(),
                accountIter.getNo(),
                accountIter.getBalance(),
                accountIter.getMoney_type(),
                accountIter.getUser().getId()
        )).collect(Collectors.toList());
    }
}