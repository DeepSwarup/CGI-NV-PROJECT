package com.bank.bankApp.repository;

import com.bank.bankApp.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPhoneNo(String phoneNo);
    Optional<Customer> findByUser_Id(Long userId);
}
