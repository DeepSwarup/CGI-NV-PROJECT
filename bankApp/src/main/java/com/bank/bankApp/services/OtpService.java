package com.bank.bankApp.services;

import com.bank.bankApp.config.OtpProperties;
import com.bank.bankApp.entity.Otp;
import com.bank.bankApp.repository.OtpRepository;
import com.sendgrid.RateLimitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpProperties props;

    private final SecureRandom random = new SecureRandom();

    private String generateNumericCode(int length){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<length; i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Transactional
    public Otp sendOtp(Long userId, String userEmail, String type) throws Exception {
        Instant now = Instant.now();

        Optional<Otp> lastOtp = otpRepository.findTopByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
        if(lastOtp.isPresent()){
            Otp last = lastOtp.get();
            if(
                    last.getLastSentAt()!=null &&
                            now.minusSeconds(props.getResendMinSeconds()).isBefore(last.getLastSentAt())
            ){
                throw new Exception("OTP recently sent. Wait before requesting again.");
            }
        }

        String code = generateNumericCode(props.getLength());
        Otp otp = new Otp();

        otp.setUserId(userId);
        otp.setType(type);
        otp.setCode(code);
        otp.setCreatedAt(now);
        otp.setExpiresAt(now.plusSeconds(props.getTtlSeconds()));
        otp.setConsumed(false);
        otp.setAttempts(0);
        otp.setLastSentAt(now);

        otpRepository.save(otp);

        String subject = "Your OTP for "+ type;
        String body = emailBodyForOtp(code, type, props.getTtlSeconds());

        emailService.sendEmail(userEmail, subject, body);

        return otp;
    }


    private String emailBodyForOtp(String code, String type, long ttlSeconds) {
        return "Dear Customer,\n\n" +
                "Your OTP for " + type + " is: " + code + "\n" +
                "It will expire in " + (ttlSeconds / 60) + " minutes.\n\n" +
                "If you did not request this, please contact support.\n\n" +
                "Regards,\nYour Bank";
    }

    @Transactional
    public boolean verifyOtp(Long userId, String type, String code) throws Exception {
        Instant now = Instant.now();
        List<Otp> validOtps = otpRepository.findByUserIdAndTypeAndConsumedFalseAndExpiresAtAfter(userId, type, now);

        if(validOtps.isEmpty()){
            throw new Exception("No valid OTP found or OTP expired");
        }
        Otp otp = validOtps.stream()
                .sorted((a,b)->b.getCreatedAt().compareTo(a.getCreatedAt()))
                .findFirst()
                .get();

        if(otp.getAttempts()>=props.getMaxVerifyAttempts()){
            throw new Exception("Too many verifications attempts");
        }

        otp.setAttempts(otp.getAttempts()+1);

        if(otp.getCode().equals(code)){
            otp.setConsumed(true);
            otpRepository.save(otp);
            return true;
        }else{
            otpRepository.save(otp);
            return false;
        }
    }

    @Transactional
    public void removeExpiredOtps() {
        List<Otp> expired = otpRepository.findByExpiresAtBefore(Instant.now());
        otpRepository.deleteAll(expired);
    }

}
