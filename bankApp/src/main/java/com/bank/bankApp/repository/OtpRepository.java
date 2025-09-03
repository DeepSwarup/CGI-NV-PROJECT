package com.bank.bankApp.repository;

import com.bank.bankApp.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findTopByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type);
    List<Otp> findByUserIdAndTypeAndConsumedFalseAndExpiresAtAfter(Long userId, String type, Instant now);
    // optional cleanup query or use scheduled cleanup
    List<Otp> findByExpiresAtBefore(Instant now);

}
