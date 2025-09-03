package com.bank.bankApp.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "term_accounts")
@PrimaryKeyJoinColumn(name = "account_id")
public class TermAccount extends Account {

    private double amount;

    private int months;

    private double penaltyAmount;

    // Manual getter methods
    public double getAmount() {
        return amount;
    }

    public int getMonths() {
        return months;
    }

    public double getPenaltyAmount() {
        return penaltyAmount;
    }

    // Manual setter methods
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public void setPenaltyAmount(double penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    // Custom builder method
    public static TermAccountBuilder builder() {
        return new TermAccountBuilder();
    }

    public static class TermAccountBuilder {
        private double amount;
        private int months;
        private double penaltyAmount;
        private double interestRate;
        private double balance;
        private LocalDate dateOfOpening;
        private Customer customer;

        public TermAccountBuilder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public TermAccountBuilder months(int months) {
            this.months = months;
            return this;
        }

        public TermAccountBuilder penaltyAmount(double penaltyAmount) {
            this.penaltyAmount = penaltyAmount;
            return this;
        }

        public TermAccountBuilder interestRate(double interestRate) {
            this.interestRate = interestRate;
            return this;
        }

        public TermAccountBuilder balance(double balance) {
            this.balance = balance;
            return this;
        }

        public TermAccountBuilder dateOfOpening(LocalDate dateOfOpening) {
            this.dateOfOpening = dateOfOpening;
            return this;
        }

        public TermAccountBuilder customer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public TermAccount build() {
            TermAccount account = new TermAccount();
            account.setAmount(amount);
            account.setMonths(months);
            account.setPenaltyAmount(penaltyAmount);
            
            // Call the parent class setters directly
            account.setInterestRate(interestRate);
            account.setBalance(balance);
            account.setDateOfOpening(dateOfOpening);
            account.setCustomer(customer);
            
            return account;
        }
    }
}