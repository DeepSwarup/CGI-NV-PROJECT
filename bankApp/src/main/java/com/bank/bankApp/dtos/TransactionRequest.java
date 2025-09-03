package com.bank.bankApp.dtos;

import com.bank.bankApp.enums.TransactionType;

public class TransactionRequest {
    private Long accountId;
    private double amount;
    private TransactionType transactiontype;
    private String transactionRemarks;

    // Constructors
    public TransactionRequest() {}

    public TransactionRequest(Long accountId, double amount, TransactionType transactiontype, String transactionRemarks) {
        this.accountId = accountId;
        this.amount = amount;
        this.transactiontype = transactiontype;
        this.transactionRemarks = transactionRemarks;
    }

    // Getters and Setters
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public TransactionType getTransactiontype() { return transactiontype; }
    public void setTransactiontype(TransactionType transactiontype) { this.transactiontype = transactiontype; }

    public String getTransactionRemarks() { return transactionRemarks; }
    public void setTransactionRemarks(String transactionRemarks) { this.transactionRemarks = transactionRemarks; }
}