package com.bank.bankApp.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendRealEmail() throws Exception{
        emailService.sendEmail(
                "deepswarup1@gmail.com",
                "Banking mail test",
                "Hello this is a test mail from banking app"
        );
    }
}
