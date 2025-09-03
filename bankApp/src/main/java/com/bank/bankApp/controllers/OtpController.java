package com.bank.bankApp.controllers;

import com.bank.bankApp.dtos.GenerateOtpRequest;
import com.bank.bankApp.dtos.VerifyOtpRequest;
import com.bank.bankApp.entity.Otp;
import com.bank.bankApp.entity.User;
import com.bank.bankApp.repository.UserRepository;
import com.bank.bankApp.services.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/generate")
    public ResponseEntity<?> generateOtp(@RequestBody GenerateOtpRequest otpRequest, HttpServletRequest request){

        try {
            Long userId = (Long) request.getAttribute("userId");
            User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));
            Otp otp = otpService.sendOtp(userId,user.getEmail(),otpRequest.getType());
            return ResponseEntity.ok(Map.of("message", "OTP sent", "expiresAt", otp.getExpiresAt()));
        } catch (Exception e) {
            return new ResponseEntity<>("Could not send OTP", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest otpRequest, HttpServletRequest request){

        try {
            Long userId = (Long) request.getAttribute("userId");
            User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));
            boolean ok = otpService.verifyOtp(userId,otpRequest.getType(),otpRequest.getCode());

            if(ok){
             return ResponseEntity.ok(Map.of("verified", true));
            }
            return ResponseEntity.status(400).body(Map.of("verified", false, "message", "Invalid OTP"));
        } catch (Exception e) {
            return new ResponseEntity<>("Could not send OTP", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
