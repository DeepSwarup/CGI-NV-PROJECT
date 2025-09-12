package com.bank.bankApp.mapper;

import com.bank.bankApp.dtos.TransactionRequest;
import com.bank.bankApp.dtos.TransactionResponse;
import com.bank.bankApp.entity.Account;
import com.bank.bankApp.entity.Transaction;

public class TransactionMapper {

    // Entity -> DTO
    public static TransactionResponse toDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .accountId(transaction.getBankAccount() != null ? transaction.getBankAccount().getAccountId() : null)
                .amount(transaction.getAmount())
                .transactiontype(transaction.getTransactionType())
                .transactionstatus(transaction.getTransactionStatus())
                .transactionDateandTime(transaction.getTransactionDateandTime())
                .transactionRemarks(transaction.getTransactionRemarks())
                .build();
    }

    // DTO -> Entity
    public static Transaction toEntity(TransactionRequest dto, Account account) {
        Transaction transaction = new Transaction();
        transaction.setBankAccount(account);
        transaction.setAmount(dto.getAmount());
        transaction.setTransactionType(dto.getTransactiontype());
        transaction.setTransactionRemarks(dto.getTransactionRemarks());
        // status & date set in service
        return transaction;
    }
}