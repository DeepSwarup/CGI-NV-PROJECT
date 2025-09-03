package com.bank.bankApp.dtos;

public class SavingsAccountRequest {
    private double minBalance;
    private double initialDeposit;
    private Long customerId;

    // Constructors
    public SavingsAccountRequest() {}

    public SavingsAccountRequest(double minBalance, double initialDeposit, Long customerId) {
        this.minBalance = minBalance;
        this.initialDeposit = initialDeposit;
        this.customerId = customerId;
    }

    // Getters and Setters
    public double getMinBalance() { return minBalance; }
    public void setMinBalance(double minBalance) { this.minBalance = minBalance; }

    public double getInitialDeposit() { return initialDeposit; }
    public void setInitialDeposit(double initialDeposit) { this.initialDeposit = initialDeposit; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}