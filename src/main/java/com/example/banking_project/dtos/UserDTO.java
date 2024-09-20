package com.example.banking_project.dtos;


import com.example.banking_project.entities.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UserDTO {

    private Long id;


    private Date createdAt;

    private Date updatedAt;
    private String name;
    private String surname;

    private String mail;

    private String phone;
    private List<String> accounts;

    private Role role;
}
