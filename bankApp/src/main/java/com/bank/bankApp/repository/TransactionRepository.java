package com.bank.bankApp.repository;

import com.bank.bankApp.entity.Account;
import com.bank.bankApp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByBankAccount(Account account);

    List<Transaction> findByBankAccountAndTransactionDateandTimeBetween(
            Account account,
            LocalDateTime start,
            LocalDateTime end
    );
}