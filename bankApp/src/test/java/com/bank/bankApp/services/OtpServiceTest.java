package com.bank.bankApp.services;

import com.bank.bankApp.entity.Otp;
import com.bank.bankApp.repository.OtpRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class OtpServiceTest {

    @Autowired
    OtpService otpService;

    @Autowired
    OtpRepository otpRepository;

    @MockBean
    EmailService emailService;

    @Test
    void testSendAndVerify() {
        Long userId = 1L;
        String email = "test@gmail.com";

        try {
            // send OTP
            Otp otp = otpService.sendOtp(userId, email, "TRANSFER");

            // flush to make sure OTP is persisted before verification
            otpRepository.flush();

            // verify with correct code
            boolean ok = otpService.verifyOtp(userId, "TRANSFER", otp.getCode());
            assertTrue(ok, "OTP should verify successfully");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
