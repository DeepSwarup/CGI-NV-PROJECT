package com.bank.bankApp.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "otp")
@Data
public class OtpProperties {
    private int length = 6;
    private long ttlSeconds = 300;
    private int maxDailySends = 10;
    private long resendMinSeconds = 30;
    private int maxVerifyAttempts = 5;
}
