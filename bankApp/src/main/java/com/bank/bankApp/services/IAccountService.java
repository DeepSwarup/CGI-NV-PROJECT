package com.bank.bankApp.services;

import com.bank.bankApp.dtos.AccountResponse;
import com.bank.bankApp.dtos.SavingsAccountRequest;
import com.bank.bankApp.dtos.TermAccountRequest;
import com.bank.bankApp.dtos.TransactionResponse;
import com.bank.bankApp.entity.SavingsAccount;
import com.bank.bankApp.entity.TermAccount;
import com.bank.bankApp.enums.AccountStatus; 

import java.util.Set;

public interface IAccountService {
    
    AccountResponse addSavingsAccount(SavingsAccountRequest request);
    AccountResponse addTermAccount(TermAccountRequest request);
    TransactionResponse transferMoney(Long senderAccountId, Long receiverAccountId, 
                                     double amount, String username, String password);
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
}