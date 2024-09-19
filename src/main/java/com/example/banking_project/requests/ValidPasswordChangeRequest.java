package com.example.banking_project.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidPasswordChangeRequest {
    private String code;
    private String mail;
    private String newPassword;
}
