package com.bank.bankApp.dtos;

// Using Lombok for boilerplate code
import lombok.Data;

@Data
public class TransferRequest {
    private Long senderAccountId;
    private Long receiverAccountId;
    private double amount;
    private String remarks;
}