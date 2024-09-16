package com.example.banking_project.services;

import com.example.banking_project.entities.Account;
import com.example.banking_project.entities.User;
import com.example.banking_project.exceptions.CreditCardNumberException;
import com.example.banking_project.exceptions.ResourceExistException;
import com.example.banking_project.exceptions.UnauthorizedActionError;
import com.example.banking_project.repos.AccountRepository;
import com.example.banking_project.repos.UserRepository;
import com.example.banking_project.requests.AccAddBalanceReq;
import com.example.banking_project.requests.CreateAccountRequest;
import com.example.banking_project.requests.DelAccRequest;
import com.example.banking_project.dtos.AccountDTO;
import com.example.banking_project.responses.AccOwnerResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private static final Random ACC_NO_GEN = new Random();
    private static final Integer ACC_GEN_BOUNDARY = 1_000_000_000;

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;


    @Transactional
    public AccountDTO create(@Valid @RequestBody CreateAccountRequest request) {
        // Extract the user phone number from the token
        String userPhone = extractPhone();
        // Fetch the user based on the phone number
        User user = extractUserInfo(userPhone);

        // Build the new account associated with the fetched user
        var newAccount = Account.builder()
                .no(generateAccountNo())
                .user(user) // Set the user
                .balance(0)
                .money_type(request.getMoney_type())
                .build();

        // Save the new account to the database
        accountRepository.save(newAccount);
        log.info("Account generated and saved to the database.");

        // Build the response
        // This response may change as success response message, or a new pop-up can be shown in the client app.
        return AccountDTO.builder()
                .id(newAccount.getId())
                .balance(newAccount.getBalance())
                .money_type(newAccount.getMoney_type())
                .no(newAccount.getNo())
                .userId(user.getId())
                .build();
    }

    public List<AccountDTO> getAccounts(String userPhone){

        //Extracting unique data from request
        //Validate that current user has permission to see the accounts
        String validationPhone = extractPhone();

        checkPermission(validationPhone, userPhone);

        User user = extractUserInfo(userPhone);

        List<Account> accounts = getAccountsByUserId(user.getId());

        //Returns the list of AccountResponse DTOs
        return accounts.stream().map(account -> new AccountDTO(
                account.getId(),
                account.getNo(),
                account.getBalance(),
                account.getMoney_type(),
                account.getUser().getId()
        )).collect(Collectors.toList());
    }

    private void checkPermission(String validationPhone, String userPhone){
        log.info(validationPhone, userPhone);
        if(!Objects.equals(validationPhone, userPhone)){
            log.error("User does not have permission to create account");
            throw new IllegalArgumentException("User does not authorized to reach to the accounts.");
        }
    }

    private User extractUserInfo(String userPhone){
        //Finding user with specified unique value.
        Optional<User> userOpt = userRepository.findByPhone(userPhone);

        //Extracting from Optional Data Type
        return extractData(userOpt);
    }

    private List<Account> extractAccounts(Optional<List<Account>> accountsOpt) {
        if (accountsOpt.isEmpty() || ArrayUtils.isEmpty(accountsOpt.get().toArray())){
            log.error("No account found!");
            throw new ResourceExistException("Account list is empty");
        }
        return accountsOpt.get();
    }

    private List<Account> getAccountsByUserId(Long userId){
        //Finding accounts that belongs to the user.
        Optional<List<Account>> accountsOpt = accountRepository.findByUserId(userId);

        //Extracting account or accounts.
        return extractAccounts(accountsOpt);
    }

    private <T> T extractData(Optional<T> optionalT){
        //Checking whether user has optional data or not.
        if(optionalT.isEmpty()){
            log.error("The session does not exist");
            throw new IllegalArgumentException("Session does not found!");
        }
        return optionalT.get();
    }

    private static String generateAccountNo(){
        //Debug can be added after fetching all account numbers
        //with an if check
        return String.format("%010d", ACC_NO_GEN.nextInt(ACC_GEN_BOUNDARY));
    }

    private Account extractAccByNo(String accNo){

            Optional<Account> accOpt =  accountRepository.findByNo(accNo);

            if (accOpt.isEmpty()){
                throw new ResourceExistException("Account does not exist");
            }

            return accOpt.get();
    }


    @Transactional
    public AccountDTO addBalance(AccAddBalanceReq request){
        log.info(request.getExpirationDate());
        log.info(request.getCardNumber());
        checkCardExpirationDate(request.getExpirationDate());
        checkCreditCardNo(request.getCardNumber());


        Account account = extractAccByNo(request.getAccountNo());
        // Search for an annotation to use for this function.
        //Setting new balance to the account
        account.setBalance(account.getBalance()+ request.getBalanceChange());
        log.info(String.valueOf(request.getBalanceChange()));
        log.info(String.valueOf(account.getBalance()));
        accountRepository.save(account);

        // This side may change or add a new pop-up can be shown in the client app.
        Account returnedAccount = extractAccByNo(request.getAccountNo());
        return AccountDTO.builder()
                .userId(returnedAccount.getUser().getId())
                .balance(returnedAccount.getBalance())
                .id(returnedAccount.getId())
                .money_type(returnedAccount.getMoney_type())
                .no(returnedAccount.getNo())
                .build();
    }

    private String extractPhone(){
        /*
            This code retrieves the currently authenticated user's information (as a UserDetails object) from the
            security context in a Spring Security application. It allows you to access the logged-in user's details,
            such as their username and roles.
        */
        //User JWT is provided by Bearer part.
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Returns response entity UNAUTHORIZED for the user that does not have JWT.
        if (userDetails == null) {
            throw new IllegalArgumentException("User not found!");
        }
        log.info("User found!");

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
            throw new ResourceExistException("Account has balance. Try to transfer it to the different account.");
        }
    }

    @Transactional
    public List<AccountDTO> deleteAccount(DelAccRequest request) {
        //CHANGE IS MANDATORY
        //Extracting unique user claim
        String userPhone = extractPhone();

        Account account = extractAccByNo(request.getNo());

        //Verify that account is owned by userPhone
        validateAccOwner(account, userPhone);

        //Verify that Account balance is equal to 0
        validateAccBalance(account, (float)0);

        //Remove from db
        accountRepository.removeByNo(account.getNo());

        //Extracting the account whose owned by current session user.
        List<Account> accounts = getAccountsByUserId(account.getUser().getId());

        //Return the accounts (if they already exists)
        return accounts.stream().map(currAcc -> new AccountDTO(
                currAcc.getId(),
                currAcc.getNo(),
                currAcc.getBalance(),
                currAcc.getMoney_type(),
                currAcc.getUser().getId()
        )).collect(Collectors.toList());
    }

    public AccOwnerResponse searchOwner(String accountNo) throws UnauthorizedActionError {
        String ownerPhone = extractPhone();
        Account account = extractAccByNo(accountNo);
        checkAccountOwner(ownerPhone, account.getUser().getPhone());


        // Returning name and surname of the account owner if it exits.
        return AccOwnerResponse.builder()
                .name(account.getUser().getName())
                .surname(account.getUser().getSurname())
                .build();
    }

    private void checkAccountOwner(String ownerPhone, String userPhone) throws UnauthorizedActionError {
        if(!Objects.equals(ownerPhone, userPhone)){
            throw new UnauthorizedActionError("User does not have permission to see this account information");
        }
    }

    public String getAccountOwner(String accountNo) {
        Account account = extractAccByNo(accountNo);
        String username = account.getUser().getName() + " " + account.getUser().getSurname();
        return Arrays.stream(username.split(" "))
                .map(s -> s.charAt(0) + "*".repeat(s.length() - 1))
                .collect(Collectors.joining(" "));
    }

    private void checkCardExpirationDate(String expirationDate) {
        DateFormat dateFormat = new SimpleDateFormat("MM/yy");

        Date cardDate = null;
        try {
            cardDate = dateFormat.parse(expirationDate);
        } catch (ParseException e) {
            throw new CreditCardNumberException("Enter a valid date!");
        }
        Date currentDate = new Date();

        if(currentDate.after(cardDate)){
            throw new CreditCardNumberException("Your card is out of date");
        }
    }

    private void checkCreditCardNo(String creditCard){
        Integer digitAmount = creditCard.length();

        Integer digitSum = 0;
        Boolean isSecond = false;

        for(int i = digitAmount - 1; i >=0; i--){
            int digit = creditCard.charAt(i) - '0';
            if(isSecond){
                digit = digit * 2;
            }

            digitSum += digit / 10;
            digitSum += digit % 10;

            isSecond = !isSecond;
        }
        if(digitSum % 10 != 0){
            log.error("Credit card number error");
            throw new CreditCardNumberException("Credit card number is invalid");
        }
    }
}