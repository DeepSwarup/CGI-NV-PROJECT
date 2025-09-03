package com.bank.bankApp.dtos;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String type;
    private String code;
}
