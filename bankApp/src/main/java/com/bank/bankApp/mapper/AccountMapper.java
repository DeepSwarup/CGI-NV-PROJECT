package com.bank.bankApp.mapper;

import com.bank.bankApp.dtos.AccountResponse;
import com.bank.bankApp.entity.Account;
import com.bank.bankApp.entity.Customer;
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
        
        // This is the defensive part. We get the customer first.
        Customer customer = account.getCustomer();
        Long customerId = (customer != null) ? customer.getCustomerId() : null;

        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .interestRate(account.getInterestRate())
                .balance(account.getBalance())
                .dateOfOpening(account.getDateOfOpening())
                .accountType(accountType)
                .status(account.getStatus())
                .customerId(customerId) // Use the safe customerId variable
                .build();
    }
}
