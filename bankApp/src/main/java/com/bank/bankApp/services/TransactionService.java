package com.bank.bankApp.services;

import com.bank.bankApp.dtos.TransactionRequest;
import com.bank.bankApp.dtos.TransactionResponse;
import com.bank.bankApp.entity.Account;
import com.bank.bankApp.entity.Customer;
import com.bank.bankApp.entity.Transaction;
import com.bank.bankApp.enums.TransactionStatus;
import com.bank.bankApp.enums.TransactionType;
import com.bank.bankApp.mapper.TransactionMapper;
import com.bank.bankApp.repository.AccountRepository;
import com.bank.bankApp.repository.CustomerRepository;
import com.bank.bankApp.repository.TransactionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public TransactionResponse createTransaction(Long userId, TransactionRequest dto) {
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getCustomer().getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        Transaction transaction = TransactionMapper.toEntity(dto, account);
        transaction.setTransactionDateandTime(LocalDateTime.now());
        transaction.setTransactionStatus(TransactionStatus.SUCCESS);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionMapper.toDTO(savedTransaction);
    }

     public Set<TransactionResponse> getAllUserTransactionsBetweenDates(Long userId, LocalDate from, LocalDate to) {
        Customer customer = customerRepository.findByUser_Id(userId)
            .orElseThrow(() -> new RuntimeException("Customer profile not found for user."));

        Set<TransactionResponse> allTransactions = new HashSet<>();
        List<Account> userAccounts = accountRepository.findByCustomer_CustomerId(customer.getCustomerId());

        for (Account account : userAccounts) {
            allTransactions.addAll(listAllTransactions(userId, account.getAccountId(), from, to));
        }
        return allTransactions;
    }

    public TransactionResponse viewTransaction(Long userId, long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        Account account = transaction.getBankAccount();

        if (!account.getCustomer().getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        return TransactionMapper.toDTO(transaction);
    }

    public TransactionResponse findTransactionById(Long userId, long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        Account account = transaction.getBankAccount();
        
        if (!account.getCustomer().getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        return TransactionMapper.toDTO(transaction);
    }

    public Set<TransactionResponse> listAllTransactions(Long userId, long accountId,
                                                        LocalDate from, LocalDate to) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getCustomer().getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59);

        return transactionRepository
                .findByBankAccountAndTransactionDateandTimeBetween(account, start, end)
                .stream()
                .map(TransactionMapper::toDTO)
                .collect(Collectors.toSet());
    }

    public Set<TransactionResponse> getAllMyAccTransactions(Long userId, long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

      
        if (!account.getCustomer().getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        return transactionRepository.findByBankAccount(account)
                .stream()
                .map(TransactionMapper::toDTO)
                .collect(Collectors.toSet());
    }

     public Set<TransactionResponse> findAllByTransactionType(TransactionType type) {
        return transactionRepository.findAllByTransactionType(type)
                .stream()
                .map(TransactionMapper::toDTO)
                .collect(Collectors.toSet());
    }
}