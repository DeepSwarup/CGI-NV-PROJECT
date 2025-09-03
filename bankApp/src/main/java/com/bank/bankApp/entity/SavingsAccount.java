package com.bank.bankApp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "savings_accounts")
@PrimaryKeyJoinColumn(name = "account_id")
public class SavingsAccount extends Account {

    private double minBalance;
    private double fine;

    // Constructors
    public SavingsAccount() {}

    public SavingsAccount(double minBalance, double fine) {
        this.minBalance = minBalance;
        this.fine = fine;
    }

    // Getters and Setters
    public double getMinBalance() { return minBalance; }
    public void setMinBalance(double minBalance) { this.minBalance = minBalance; }

    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }

    // Custom builder method
    public static SavingsAccountBuilder builder() {
        return new SavingsAccountBuilder();
    }

    public static class SavingsAccountBuilder {
        private double minBalance;
        private double fine;
        private double interestRate;
        private double balance;
        private LocalDate dateOfOpening;
        private Customer customer;

        public SavingsAccountBuilder minBalance(double minBalance) {
            this.minBalance = minBalance;
            return this;
        }

        public SavingsAccountBuilder fine(double fine) {
            this.fine = fine;
            return this;
        }

        public SavingsAccountBuilder interestRate(double interestRate) {
            this.interestRate = interestRate;
            return this;
        }

        public SavingsAccountBuilder balance(double balance) {
            this.balance = balance;
            return this;
        }

        public SavingsAccountBuilder dateOfOpening(LocalDate dateOfOpening) {
            this.dateOfOpening = dateOfOpening;
            return this;
        }

        public SavingsAccountBuilder customer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public SavingsAccount build() {
            SavingsAccount account = new SavingsAccount();
            account.setMinBalance(minBalance);
            account.setFine(fine);
            account.setInterestRate(interestRate);
            account.setBalance(balance);
            account.setDateOfOpening(dateOfOpening);
            account.setCustomer(customer);
            return account;
        }
    }
}