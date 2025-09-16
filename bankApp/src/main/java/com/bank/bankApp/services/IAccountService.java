package com.bank.bankApp.services;

import com.bank.bankApp.dtos.*;
import com.bank.bankApp.enums.AccountStatus;
import java.util.Set;

public interface IAccountService {
    
    AccountResponse addSavingsAccount(SavingsAccountRequest request);
    AccountResponse addTermAccount(TermAccountRequest request);

    /**
     * CORRECTED: The signature now includes 'remarks' and 'otp' and removes the old parameters.
     * This will now match the implementation in AccountService.java.
     */
    TransactionResponse transferMoney(Long senderAccountId, Long receiverAccountId, 
                                     double amount, String remarks, String otp);

    TransactionResponse withdraw(Long accountId, double amount, String username, String password);
    TransactionResponse deposit(Long accountId, double amount);
    AccountResponse findAccountById(Long accountId);
    Set<AccountResponse> viewAccounts(Long customerId);
    AccountResponse viewSavingAcc(Long customerId);
    AccountResponse viewTermAcc(Long customerId);
    boolean closeSavingsAccount(Long accountId);
    boolean closeTermAccount(Long accountId);
    void computeInterestForAllAccounts();
    AccountResponse updateAccountStatus(Long accountId, AccountStatus status);
    AccountResponse updateInterestRate(Long accountId, double newInterestRate);
    AccountResponse approveTermAccount(Long accountId, AccountApprovalRequest request);
    TransactionResponse creditInterest(Long accountId);
}

