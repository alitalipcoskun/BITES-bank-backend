package com.example.banking_project.repos;

import com.example.banking_project.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<List<Account>> findByUserId(long userId);
    Optional<Account> findById(long id);
    Optional<Account> findByNo(String no);

    default void removeByNo(String no){
        Optional<Account> accountOpt = findByNo(no);
        Account account = extractData(accountOpt);
        deleteById(Math.toIntExact(account.getId()));
    }
    private <T> T extractData(Optional<T> optionalT){
        //The function is for extracting all the data from Optional data.
        if(optionalT.isEmpty()){
            throw new IllegalArgumentException("Return type is empty.");
        }
        return optionalT.get();
    }
}