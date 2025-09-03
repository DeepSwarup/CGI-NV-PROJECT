package com.bank.bankApp.scheduler;

import com.bank.bankApp.services.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class MonthlyEmailSchedulerTest {

    @Autowired
    private MonthlyEmailScheduler scheduler;

    @MockBean
    private EmailService emailService;

    @Test
    void testSendMonthlySummary() throws IOException {
        scheduler.sendMonthlySummary();

//        verify(emailService, times(1)).sendEmail(
//                eq("deepswarup1@gmail.com"),
//                eq("Monthly update"),
//                contains("Dear Customer")
//        );
    }
}
