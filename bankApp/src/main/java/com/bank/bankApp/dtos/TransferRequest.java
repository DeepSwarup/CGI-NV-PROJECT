package com.bank.bankApp.dtos;

// Using manual getters/setters to avoid Lombok issues
public class TransferRequest {
    private Long senderAccountId;
    private Long receiverAccountId;
    private double amount;
    private String remarks;
    private String otp; // Add this field for the One-Time Password

    // --- Getters and Setters ---
    public Long getSenderAccountId() { return senderAccountId; }
    public void setSenderAccountId(Long senderAccountId) { this.senderAccountId = senderAccountId; }
    public Long getReceiverAccountId() { return receiverAccountId; }
    public void setReceiverAccountId(Long receiverAccountId) { this.receiverAccountId = receiverAccountId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}

