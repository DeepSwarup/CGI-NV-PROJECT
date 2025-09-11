package com.bank.bankApp.services;

import com.bank.bankApp.dtos.*;
import com.bank.bankApp.entity.*;
import com.bank.bankApp.enums.AccountStatus;
import com.bank.bankApp.enums.TransactionStatus;
import com.bank.bankApp.enums.TransactionType;
import com.bank.bankApp.mapper.AccountMapper;
import com.bank.bankApp.mapper.TransactionMapper;
import com.bank.bankApp.repository.AccountRepository;
import com.bank.bankApp.repository.CustomerRepository;
import com.bank.bankApp.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import com.bank.bankApp.utils.AccountNumberGenerator;

@Service
public class AccountService implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountNumberGenerator accountNumberGenerator;

    private Long generateUniqueAccountNumber() {
        long newAccountId;
        do {
            newAccountId = accountNumberGenerator.generate();
        } while (accountRepository.existsById(newAccountId)); // Keep generating until a unique one is found
        return newAccountId;
    }

   @Override
    @Transactional
    public AccountResponse updateAccountStatus(Long accountId, AccountStatus status) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));
        
        account.setStatus(status);
        Account updatedAccount = accountRepository.save(account);
        
        return AccountMapper.toDTO(updatedAccount);
    }


    @Override
    @Transactional
    public AccountResponse updateInterestRate(Long accountId, double newInterestRate) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));

        account.setInterestRate(newInterestRate);
        Account updatedAccount = accountRepository.save(account);

        return AccountMapper.toDTO(updatedAccount);
    }

    @Override
    @Transactional
    public AccountResponse addSavingsAccount(SavingsAccountRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        SavingsAccount account = SavingsAccount.builder()
                .minBalance(request.getMinBalance())
                .balance(request.getInitialDeposit())
                .interestRate(3.5)
                .dateOfOpening(LocalDate.now())
                .customer(customer)
                .fine(0.0)
                .build();
        account.setAccountId(generateUniqueAccountNumber());
        account.setStatus(AccountStatus.PENDING);

        SavingsAccount savedAccount = accountRepository.save(account);
        
        createTransaction(savedAccount, request.getInitialDeposit(), 
                          TransactionType.DEPOSIT, "Initial deposit");
        
        return AccountMapper.toDTO(savedAccount);
    }

    @Override
    @Transactional
    public AccountResponse addTermAccount(TermAccountRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        TermAccount account = TermAccount.builder()
                .amount(request.getAmount())
                .months(request.getMonths())
                .balance(request.getInitialDeposit())
                .interestRate(6.0)
                .dateOfOpening(LocalDate.now())
                .customer(customer)
                .penaltyAmount(0.0)
                .build();
 account.setAccountId(generateUniqueAccountNumber());
        account.setStatus(AccountStatus.PENDING); // Set initial status to PENDING

        TermAccount savedAccount = accountRepository.save(account);
        
        createTransaction(savedAccount, request.getInitialDeposit(), 
                          TransactionType.DEPOSIT, "Initial deposit");
        
        return AccountMapper.toDTO(savedAccount);
    }


    @Override
    @Transactional
    public TransactionResponse transferMoney(Long senderAccountId, Long receiverAccountId, 
                                             double amount, String username, String password) {
        Account senderAccount = accountRepository.findById(senderAccountId)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));
        
        Account receiverAccount = accountRepository.findById(receiverAccountId)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));
        
        // Add status checks
        if (senderAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Sender account is not active.");
        }
        if (receiverAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Receiver account is not active.");
        }

        if (senderAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        Transaction withdrawal = createTransaction(senderAccount, amount, 
                TransactionType.TRANSFER, "Transfer to account: " + receiverAccountId);
        
        createTransaction(receiverAccount, amount, 
                TransactionType.TRANSFER, "Transfer from account: " + senderAccountId);

        return TransactionMapper.toDTO(withdrawal);
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(Long accountId, double amount, String username, String password) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Add status check
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active.");
        }

        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction transaction = createTransaction(account, amount, 
                TransactionType.WITHDRAWAL, "Cash withdrawal");

        return TransactionMapper.toDTO(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse deposit(Long accountId, double amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        Transaction transaction = createTransaction(account, amount, 
                TransactionType.DEPOSIT, "Cash deposit");

        return TransactionMapper.toDTO(transaction);
    }

    @Override
    public AccountResponse findAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return AccountMapper.toDTO(account);
    }

    @Override
    public Set<AccountResponse> viewAccounts(Long customerId) {
        List<Account> accounts = accountRepository.findByCustomer_CustomerId(customerId);
        return accounts.stream()
                .map(AccountMapper::toDTO)
                .collect(Collectors.toSet());
    }

    @Override
    public AccountResponse viewSavingAcc(Long customerId) {
        Optional<SavingsAccount> account = accountRepository.findSavingsAccountByCustomerId(customerId);
        return account.map(AccountMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Savings account not found for customer"));
    }

    @Override
    public AccountResponse viewTermAcc(Long customerId) {
        Optional<TermAccount> account = accountRepository.findTermAccountByCustomerId(customerId);
        return account.map(AccountMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Term account not found for customer"));
    }

    @Override
    @Transactional
    public boolean closeSavingsAccount(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isPresent() && account.get() instanceof SavingsAccount) {
            accountRepository.delete(account.get());
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean closeTermAccount(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isPresent() && account.get() instanceof TermAccount) {
            accountRepository.delete(account.get());
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void computeInterestForAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        
        for (Account account : accounts) {
            double interest = calculateInterest(account);
            account.setBalance(account.getBalance() + interest);
            accountRepository.save(account);
            
            // Record interest transaction
            createTransaction(account, interest, TransactionType.DEPOSIT, "Interest credit");
        }
    }

    private double calculateInterest(Account account) {
        if (account instanceof SavingsAccount) {
            return account.getBalance() * (account.getInterestRate() / 100) / 12; // Monthly interest
        } else if (account instanceof TermAccount) {
            TermAccount termAccount = (TermAccount) account;
            return termAccount.getAmount() * (termAccount.getInterestRate() / 100) * termAccount.getMonths() / 12;
        }
        return 0;
    }

    private Transaction createTransaction(Account account, double amount, 
                                    TransactionType type, String remarks) {
    // Make sure the account is managed (persisted)
    Account managedAccount = accountRepository.findById(account.getAccountId())
        .orElseThrow(() -> new RuntimeException("Account not found in database"));
    
    Transaction transaction = new Transaction();
    transaction.setAmount(amount);
    transaction.setTransactiontype(type);
    transaction.setTransactionDateandTime(LocalDateTime.now());
    transaction.setBankAccount(managedAccount); // Use the managed entity
    transaction.setTransactionStatus(TransactionStatus.SUCCESS);
    transaction.setTransactionRemarks(remarks);
    
    return transactionRepository.save(transaction);
}
}