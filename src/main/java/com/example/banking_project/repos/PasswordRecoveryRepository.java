package com.example.banking_project.repos;

import com.example.banking_project.entities.PasswordRecovery;
import org.springframework.data.repository.CrudRepository;

public interface PasswordRecoveryRepository extends CrudRepository<PasswordRecovery, String> {
}
