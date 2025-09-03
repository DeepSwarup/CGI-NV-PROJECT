package com.bank.bankApp.repository;

import com.bank.bankApp.entity.Account;
import com.bank.bankApp.entity.SavingsAccount;
import com.bank.bankApp.entity.TermAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    List<Account> findByCustomer_CustomerId(Long customerId);
    
    @Query("SELECT a FROM SavingsAccount a WHERE a.customer.customerId = :customerId")
    Optional<SavingsAccount> findSavingsAccountByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT a FROM TermAccount a WHERE a.customer.customerId = :customerId")
    Optional<TermAccount> findTermAccountByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT a FROM Account a WHERE a.balance >= :minBalance")
    List<Account> findAccountsWithMinBalance(@Param("minBalance") double minBalance);
}