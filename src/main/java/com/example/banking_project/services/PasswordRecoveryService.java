package com.example.banking_project.services;

import com.example.banking_project.entities.PasswordRecovery;
import com.example.banking_project.entities.User;
import com.example.banking_project.exceptions.CodeNotMatchError;
import com.example.banking_project.exceptions.ExpiredTokenException;
import com.example.banking_project.exceptions.ResourceExistException;
import com.example.banking_project.exceptions.ResourceNotFoundException;
import com.example.banking_project.repos.PasswordRecoveryRepository;
import com.example.banking_project.repos.UserRepository;
import com.example.banking_project.requests.CodeValidateRequest;
import com.example.banking_project.requests.PasswordRecoveryRequest;
import com.example.banking_project.requests.ValidPasswordChangeRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordRecoveryService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordRecoveryRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserService userService;


    private static Integer RECOVERY_PSWRD_BOUND = 1000000;

    @Transactional
    public String createToken(PasswordRecoveryRequest request) {
        String token = generateRandomNumber();
        // Verify that entered e-mail is registered to the service.
        userService.findUserByMail(request.getMail());

        boolean result = searchRecoveryData(request.getMail());

        log.info(String.valueOf(result));
        if(!result){

            throw new ResourceExistException( "You've already created a token");
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

    private void saveRedis(PasswordRecovery recovery) {
        String key = "PasswordRecovery:" + recovery.getMail();
        redisTemplate.opsForHash().putAll(key, Map.of(
                "token", recovery.getToken(),
                "creationDate", recovery.getCreationDate(),
                "expirationDate", recovery.getExpirationDate(),
                "id", recovery.getId(),
                "mail", recovery.getMail()));
    }

    public String getPasswordRecoveryData(CodeValidateRequest request) throws ParseException {
        String key = "PasswordRecovery:" + request.getMail();
        Map<Object, Object> recoveryInfo = redisTemplate.opsForHash().entries(key);
        if (recoveryInfo.isEmpty()) {
            throw new ResourceNotFoundException("Validate your request, try again with going forget password.");
        }
        if (recoveryInfo.get("token").equals(request.getToken())) {
            Date expDate = strToDate((String) recoveryInfo.get("expirationDate"));
            if (expDate.after(new Date())){
                return "True";
            }
        }
        return "False";
    }




    private boolean searchRecoveryData(String mail) {
        String key = "PasswordRecovery:" + mail;
        Map<Object, Object> recoveryInfo = redisTemplate.opsForHash().entries(key);

        if (recoveryInfo.isEmpty()) {
            // No existing recovery data found
            return true;
        }

        String expirationDateStr = (String) recoveryInfo.get("expirationDate");
        try {
            Date expirationDate = strToDate(expirationDateStr);
            if (expirationDate.before(new Date())) {
                // If the token has expired, remove it and return true
                redisTemplate.delete(key);
                return true;
            }
            // Token exists and is not expired
            return false;
        } catch (ParseException e) {
            log.error("Error parsing expiration date: " + expirationDateStr, e);
            // If there's an error parsing the date, assume the entry is invalid
            redisTemplate.delete(key);
            return true;
        }
    }

    public static String generateRandomNumber() {
        Random random = new Random();
        int number = random.nextInt(RECOVERY_PSWRD_BOUND);
        log.info(String.valueOf(number));
        System.out.println(number);
        return String.format("%06d", number);
    }

    @Transactional
    public void clearExpiredRecoveryData() throws ParseException {
        Set<String> keys = redisTemplate.keys("PasswordRecovery:*");
        List<PasswordRecovery> allRecoveries = new ArrayList<>();
        long currentTimeMillis = System.currentTimeMillis();


        for (String key : keys) {
            Map<Object, Object> recoveryInfo = redisTemplate.opsForHash().entries(key);
            String expirationDateStr = (String) recoveryInfo.get("expirationDate");
    
            try {
                Date expirationDate = strToDate(expirationDateStr);
                long expirationDateMillis = expirationDate.getTime();
    
                if (expirationDateMillis < currentTimeMillis) {
                    redisTemplate.delete(key);
                }
            } catch (ParseException e) {
                log.error("Error parsing expiration date: " + expirationDateStr, e);
            }

        }

    }

    private Date strToDate(String dateInput) throws ParseException{
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        return dateFormat.parse(dateInput);
    }

    public String securePasswordChange(ValidPasswordChangeRequest request) throws ParseException {
        clearExpiredRecoveryData();
        String newPassword = request.getNewPassword();
        String key = "PasswordRecovery:" + request.getMail();
        // Fetching user information from database
        User user = userService.findUserByMail(request.getMail());

        // Fetching redis information
        Map<Object, Object> recoveryInfo = redisTemplate.opsForHash().entries(key);

        checkRecoveryInfo(recoveryInfo, request.getCode());

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password changed successfully!";
    }

    private void checkRecoveryInfo(Map<Object, Object> recovery, String code) throws ParseException {
        String key = "PasswordRecovery:" + (String) recovery.get("mail");

        // Getting expiration date
        String expirationDateStr = (String) recovery.get("expirationDate");

        // Getting code
        String redisCode = (String) recovery.get("token");

        Date expirationDate = strToDate(expirationDateStr);

        if (expirationDate.before(new Date())) {
            redisTemplate.delete(key);
            throw new ExpiredTokenException("Token time is expired. Start the recovery process again");
        }

        if (!redisCode.equals(code)){
            throw new CodeNotMatchError("Check the entered code again");
        }


    }
}

