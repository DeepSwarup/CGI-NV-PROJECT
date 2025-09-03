package com.bank.bankApp.scheduler;

import com.bank.bankApp.services.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class OtpCleanupScheduler {

    @Autowired
    private OtpService otpService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredOtps(){
        otpService.removeExpiredOtps();
    }
}
