package com.example.banking_project.repos;

import com.example.banking_project.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository//While extending JPA Repository, Entity class and its primary key data type must be added.
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByPhone(String phone);
    Optional<User> findByMail(String mail);
}
