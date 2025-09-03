package com.bank.bankApp.dtos;

import com.bank.bankApp.enums.AccountType;
import java.time.LocalDate;

public class AccountResponse {
    private Long accountId;
    private double interestRate;
    private double balance;
    private LocalDate dateOfOpening;
    private AccountType accountType;
    private Long customerId;

    // Constructors
    public AccountResponse() {}

    public AccountResponse(Long accountId, double interestRate, double balance, 
                          LocalDate dateOfOpening, AccountType accountType, Long customerId) {
        this.accountId = accountId;
        this.interestRate = interestRate;
        this.balance = balance;
        this.dateOfOpening = dateOfOpening;
        this.accountType = accountType;
        this.customerId = customerId;
    }

    // Getters and Setters
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public LocalDate getDateOfOpening() { return dateOfOpening; }
    public void setDateOfOpening(LocalDate dateOfOpening) { this.dateOfOpening = dateOfOpening; }

    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    // Manual builder
    public static AccountResponseBuilder builder() {
        return new AccountResponseBuilder();
    }

    public static class AccountResponseBuilder {
        private Long accountId;
        private double interestRate;
        private double balance;
        private LocalDate dateOfOpening;
        private AccountType accountType;
        private Long customerId;

        public AccountResponseBuilder accountId(Long accountId) {
            this.accountId = accountId;
            return this;
        }

        public AccountResponseBuilder interestRate(double interestRate) {
            this.interestRate = interestRate;
            return this;
        }

        public AccountResponseBuilder balance(double balance) {
            this.balance = balance;
            return this;
        }

        public AccountResponseBuilder dateOfOpening(LocalDate dateOfOpening) {
            this.dateOfOpening = dateOfOpening;
            return this;
        }

        public AccountResponseBuilder accountType(AccountType accountType) {
            this.accountType = accountType;
            return this;
        }

        public AccountResponseBuilder customerId(Long customerId) {
            this.customerId = customerId;
            return this;
        }

        public AccountResponse build() {
            return new AccountResponse(accountId, interestRate, balance, dateOfOpening, accountType, customerId);
        }
    }
}