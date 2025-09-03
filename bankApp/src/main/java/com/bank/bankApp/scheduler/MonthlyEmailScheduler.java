package com.bank.bankApp.scheduler;


import com.bank.bankApp.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class MonthlyEmailScheduler {

    @Autowired
    private EmailService emailService;



//    @Scheduled(cron = "0 * * * * ?")  // every 1 minute
    @Scheduled(cron = "59 59 23 L * ?")
    public void sendMonthlySummary() throws IOException {
        log.info("Monthly scheduler triggered");

        String customerEmail = "deepswarup1@gmail.com";
        String subject = "Monthly update";
        double calculateInterest = 1000;
        double calculateFine = 10000;
        double calculatePenalty = 25467;

        String messageBody = "Dear Customer your interset for this month is: "+calculateInterest;

        emailService.sendEmail(customerEmail, subject, messageBody);

    }
}
