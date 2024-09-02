package com.example.banking_project.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccAddBalanceReq {
    private String accountNo;
    private float balanceChange;
}
