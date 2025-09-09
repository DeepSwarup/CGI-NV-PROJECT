package com.bank.bankApp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.bank.bankApp.enums.TransactionStatus;
import com.bank.bankApp.enums.TransactionType;

@Entity
@Table(name="transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long transactionId;
    
    @Column(nullable = false)
    private double amount;
    
    @Enumerated(EnumType.STRING)
    private TransactionType transactiontype;
    
    @Column(name = "transaction_date_and_time", nullable = false)
    private LocalDateTime transactionDateandTime;
    
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account bankAccount;
    
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;
    
    private String transactionRemarks;

    // Getters and Setters
    public long getTransactionId() { return transactionId; }
    public void setTransactionId(long transactionId) { this.transactionId = transactionId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public TransactionType getTransactiontype() { return transactiontype; }
    public void setTransactiontype(TransactionType transactiontype) { this.transactiontype = transactiontype; }

    public LocalDateTime getTransactionDateandTime() { return transactionDateandTime; }
    public void setTransactionDateandTime(LocalDateTime transactionDateandTime) { this.transactionDateandTime = transactionDateandTime; }

    public Account getBankAccount() { return bankAccount; }
    public void setBankAccount(Account bankAccount) { this.bankAccount = bankAccount; }

    public TransactionStatus getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(TransactionStatus transactionStatus) { this.transactionStatus = transactionStatus; }

    public String getTransactionRemarks() { return transactionRemarks; }
    public void setTransactionRemarks(String transactionRemarks) { this.transactionRemarks = transactionRemarks; }
}