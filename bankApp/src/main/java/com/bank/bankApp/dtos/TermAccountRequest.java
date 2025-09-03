package com.bank.bankApp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermAccountRequest {
    private double amount;
    private int months;
    private double initialDeposit;
    private Long customerId;
}