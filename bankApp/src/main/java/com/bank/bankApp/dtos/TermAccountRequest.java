package com.bank.bankApp.dtos;

// Lombok annotations are kept for documentation but we add manual methods
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermAccountRequest {
    // private double amount;
    private int months;
    private double initialDeposit;
    private Long customerId;

    // --- Manual Getters and Setters ---

    // public double getAmount() {
    //     return amount;
    // }

    // public void setAmount(double amount) {
    //     this.amount = amount;
    // }

    public int getMonths() {
        return months;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public double getInitialDeposit() {
        return initialDeposit;
    }

    public void setInitialDeposit(double initialDeposit) {
        this.initialDeposit = initialDeposit;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
