package com.bank.bankApp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.JOINED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    private double interestRate;

    private double balance;

    private LocalDate dateOfOpening;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> transactions;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Nominee> nominees;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Beneficiary> beneficiaries;

    // Constructors
    public Account() {}

    // Getters and Setters
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public LocalDate getDateOfOpening() { return dateOfOpening; }
    public void setDateOfOpening(LocalDate dateOfOpening) { this.dateOfOpening = dateOfOpening; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Set<Transaction> getTransactions() { return transactions; }
    public void setTransactions(Set<Transaction> transactions) { this.transactions = transactions; }

    public Set<Nominee> getNominees() { return nominees; }
    public void setNominees(Set<Nominee> nominees) { this.nominees = nominees; }

    public Set<Beneficiary> getBeneficiaries() { return beneficiaries; }
    public void setBeneficiaries(Set<Beneficiary> beneficiaries) { this.beneficiaries = beneficiaries; }

    // Method to get user from customer
    public User getUser() {
        return this.customer != null ? this.customer.getUser() : null;
    }
}