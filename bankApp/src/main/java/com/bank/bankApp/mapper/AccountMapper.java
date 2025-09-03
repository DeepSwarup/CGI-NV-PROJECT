package com.bank.bankApp.mapper;

import com.bank.bankApp.dtos.AccountResponse;
import com.bank.bankApp.entity.Account;
import com.bank.bankApp.entity.SavingsAccount;
import com.bank.bankApp.entity.TermAccount;
import com.bank.bankApp.enums.AccountType;

public class AccountMapper {

    public static AccountResponse toDTO(Account account) {
        if (account == null) {
            return null;
        }
        
        AccountType accountType = null;
        
        if (account instanceof SavingsAccount) {
            accountType = AccountType.SAVINGS;
        } else if (account instanceof TermAccount) {
            accountType = AccountType.TERM;
        }
        
        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .interestRate(account.getInterestRate())
                .balance(account.getBalance())
                .dateOfOpening(account.getDateOfOpening())
                .accountType(accountType)
                .customerId(account.getCustomer() != null ? account.getCustomer().getCustomerId() : null)
                .build();
    }
}