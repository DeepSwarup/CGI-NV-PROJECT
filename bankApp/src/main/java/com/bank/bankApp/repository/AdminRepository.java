package com.bank.bankApp.repository;

import com.bank.bankApp.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByContact(String phoneNo);
    Optional<Admin> findByUser_Id(Long userId);
}
