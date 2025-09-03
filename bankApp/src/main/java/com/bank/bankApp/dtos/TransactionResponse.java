package com.bank.bankApp.dtos;

import com.bank.bankApp.enums.TransactionStatus;
import com.bank.bankApp.enums.TransactionType;
import java.time.LocalDateTime;

public class TransactionResponse {
    private Long transactionId;
    private Long accountId;
    private double amount;
    private TransactionType transactiontype;
    private TransactionStatus transactionstatus;
    private LocalDateTime transactionDateandTime;
    private String transactionRemarks;

    // Constructors
    public TransactionResponse() {}

    public TransactionResponse(Long transactionId, Long accountId, double amount, 
                              TransactionType transactiontype, TransactionStatus transactionstatus,
                              LocalDateTime transactionDateandTime, String transactionRemarks) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.transactiontype = transactiontype;
        this.transactionstatus = transactionstatus;
        this.transactionDateandTime = transactionDateandTime;
        this.transactionRemarks = transactionRemarks;
    }

    // Getters and Setters
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public TransactionType getTransactiontype() { return transactiontype; }
    public void setTransactiontype(TransactionType transactiontype) { this.transactiontype = transactiontype; }

    public TransactionStatus getTransactionstatus() { return transactionstatus; }
    public void setTransactionstatus(TransactionStatus transactionstatus) { this.transactionstatus = transactionstatus; }

    public LocalDateTime getTransactionDateandTime() { return transactionDateandTime; }
    public void setTransactionDateandTime(LocalDateTime transactionDateandTime) { this.transactionDateandTime = transactionDateandTime; }

    public String getTransactionRemarks() { return transactionRemarks; }
    public void setTransactionRemarks(String transactionRemarks) { this.transactionRemarks = transactionRemarks; }

    // Manual builder
    public static TransactionResponseBuilder builder() {
        return new TransactionResponseBuilder();
    }

    public static class TransactionResponseBuilder {
        private Long transactionId;
        private Long accountId;
        private double amount;
        private TransactionType transactiontype;
        private TransactionStatus transactionstatus;
        private LocalDateTime transactionDateandTime;
        private String transactionRemarks;

        public TransactionResponseBuilder transactionId(Long transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public TransactionResponseBuilder accountId(Long accountId) {
            this.accountId = accountId;
            return this;
        }

        public TransactionResponseBuilder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public TransactionResponseBuilder transactiontype(TransactionType transactiontype) {
            this.transactiontype = transactiontype;
            return this;
        }

        public TransactionResponseBuilder transactionstatus(TransactionStatus transactionstatus) {
            this.transactionstatus = transactionstatus;
            return this;
        }

        public TransactionResponseBuilder transactionDateandTime(LocalDateTime transactionDateandTime) {
            this.transactionDateandTime = transactionDateandTime;
            return this;
        }

        public TransactionResponseBuilder transactionRemarks(String transactionRemarks) {
            this.transactionRemarks = transactionRemarks;
            return this;
        }

        public TransactionResponse build() {
            return new TransactionResponse(transactionId, accountId, amount, transactiontype, 
                                         transactionstatus, transactionDateandTime, transactionRemarks);
        }
    }
}