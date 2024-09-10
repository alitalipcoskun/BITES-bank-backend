package com.example.banking_project.responses;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Builder
@Setter
public class AccOwnerResponse {
    private String name;
    private String surname;
}
