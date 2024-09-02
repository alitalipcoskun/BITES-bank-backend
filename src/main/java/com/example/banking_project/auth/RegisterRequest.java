package com.example.banking_project.auth;

import com.example.banking_project.entities.User;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    //Required data to register data to our database as a user. It may change on different projects.
    @NotEmpty(message = "The name is required.")
    @Size(min=2, max=20, message = "The length of your name must be between 3 and 20 character.")
    private String name;
    @Size(min=2, max=20, message = "The length of your surname must be between 3 and 20 characters.")
    @NotEmpty(message =  "The surname is required")
    private String surname;
    @NotEmpty(message = "The mail is required")
    @Email(message = "The email address is invalid.", flags={Pattern.Flag.CASE_INSENSITIVE})
    private String mail;
    @NotEmpty(message = "The phone number is required")
    @Size(min = 10, max=10, message = "The preferred format for phone number is 5xxxxxxxxx")
    private String phone;
    @NotEmpty(message = "The password is required")
    private String password;

    public User toUser(){
        return new User()
                .setName(name)
                .setSurname(surname)
                .setMail(mail)
                .setPhone(phone)
                .setPassword(password);
    }
}
