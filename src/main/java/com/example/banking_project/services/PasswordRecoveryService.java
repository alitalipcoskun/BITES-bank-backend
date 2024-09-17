package com.example.banking_project.services;


import com.example.banking_project.entities.PasswordRecovery;
import com.example.banking_project.entities.User;
import com.example.banking_project.exceptions.ResourceNotFoundException;
import com.example.banking_project.repos.PasswordRecoveryRepository;
import com.example.banking_project.repos.UserRepository;
import com.example.banking_project.requests.CodeValidateRequest;
import com.example.banking_project.requests.PasswordRecoveryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordRecoveryService {


    private RedisTemplate<String, Object> redisTemplate;
    private final PasswordRecoveryRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    private static Integer RECOVERY_PSWRD_BOUND = 1000000;


    public String createToken(PasswordRecoveryRequest request){
        String token = generateRandomNumber();
        // Verify that entered e-mail is registered to the service.
        Optional<User> user = userRepository.findByMail(request.getMail());
        if(user.isEmpty()){
            throw new ResourceNotFoundException("User does not exist");
        }


        PasswordRecovery recovery = PasswordRecovery.builder()
                .id(UUID.randomUUID().toString())
                .creationDate(new Date().toString())
                .mail(request.getMail())
                .token(token)
                .expirationDate(String.valueOf(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5))))
                .build();

        saveRedis(recovery);

        return "Success!";
    }

    private void saveRedis(PasswordRecovery recovery){
        String key = "PasswordRecovery:" + recovery.getMail();
        redisTemplate.opsForHash().putAll(key, Map.of(
                "token", recovery.getToken(),
                "creationDate", recovery.getCreationDate(),
                "expirationDate", recovery.getExpirationDate(),
                "id", recovery.getId(),
                "mail", recovery.getMail()
        ));

    }

    public String getPasswordRecoveryData(CodeValidateRequest request){
        log.info(request.getToken());
        String key = "PasswordRecovery:"+request.getMail();
        Map<Object, Object> recoveryInfo= redisTemplate.opsForHash().entries(key);
        if(recoveryInfo.get("token").equals(request.getToken())){
            return "True";
        }
        return "False";
    }
    public static String generateRandomNumber() {
        Random random = new Random();
        int number = random.nextInt(RECOVERY_PSWRD_BOUND);
        log.info(String.valueOf(number));
        System.out.println(number);
        return String.format("%06d", number);
    }

}
