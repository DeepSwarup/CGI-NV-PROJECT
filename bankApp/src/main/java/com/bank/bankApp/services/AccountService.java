package com.bank.bankApp.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.bankApp.dtos.AccountApprovalRequest;
import com.bank.bankApp.dtos.AccountResponse;
import com.bank.bankApp.dtos.SavingsAccountRequest;
import com.bank.bankApp.dtos.TermAccountRequest;
import com.bank.bankApp.dtos.TransactionResponse;
import com.bank.bankApp.entity.Account;
import com.bank.bankApp.entity.Customer;
import com.bank.bankApp.entity.SavingsAccount;
import com.bank.bankApp.entity.TermAccount;
import com.bank.bankApp.entity.Transaction;
import com.bank.bankApp.enums.AccountStatus;
import com.bank.bankApp.enums.TransactionStatus;
import com.bank.bankApp.enums.TransactionType;
import com.bank.bankApp.mapper.AccountMapper;
import com.bank.bankApp.mapper.TransactionMapper;
import com.bank.bankApp.repository.AccountRepository;
import com.bank.bankApp.repository.CustomerRepository;
import com.bank.bankApp.repository.TransactionRepository;
import com.bank.bankApp.utils.AccountNumberGenerator;

import jakarta.transaction.Transactional;

@Service
public class AccountService implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountNumberGenerator accountNumberGenerator;

     @Override
    @Transactional
    public TransactionResponse creditInterest(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Interest can only be credited to ACTIVE accounts.");
        }

        double interestAmount = calculateInterest(account);
        
        // Ensure interest is a positive value to avoid issues
        if (interestAmount <= 0) {
            throw new RuntimeException("No interest to credit for this period.");
        }

        // Add the interest to the account balance
        account.setBalance(account.getBalance() + interestAmount);
        accountRepository.save(account);

        // Create a transaction record for this event
        Transaction interestTransaction = createTransaction(account, interestAmount, 
                                                            TransactionType.DEPOSIT, "Monthly Interest Credit");
        
        return TransactionMapper.toDTO(interestTransaction);
    }
    
    // This private helper method is already in your service, we are just reusing it.
    private double calculateInterest(Account account) {
        if (account instanceof SavingsAccount) {
            // Monthly interest calculation
            return account.getBalance() * (account.getInterestRate() / 100) / 12; 
        } else if (account instanceof TermAccount) {
            TermAccount termAccount = (TermAccount) account;
            // Full term interest calculation (for simplicity)
            // A real-world scenario might be more complex (e.g., pro-rated)
            return termAccount.getBalance() * (termAccount.getInterestRate() / 100) * termAccount.getMonths() / 12;
        }
        return 0;
    }

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

        // SIMPLIFIED: The builder now uses .balance() for the principal
        TermAccount account = TermAccount.builder()
                .months(request.getMonths())
                .balance(request.getInitialDeposit()) // Use initialDeposit for the balance
                .interestRate(6.0) // Default rate
                .dateOfOpening(LocalDate.now())
                .customer(customer)
                .penaltyAmount(0.0) // Default penalty
                .build();
                
        account.setAccountId(generateUniqueAccountNumber());
        account.setStatus(AccountStatus.PENDING);

        TermAccount savedAccount = accountRepository.save(account);
        
        createTransaction(savedAccount, request.getInitialDeposit(), 
                          TransactionType.DEPOSIT, "Initial Term Deposit");
        
        return AccountMapper.toDTO(savedAccount);
    }

     @Override
    @Transactional
    public TransactionResponse transferMoney(Long senderAccountId, Long receiverAccountId, 
                                             double amount, String remarks, String otp) {
        Account senderAccount = accountRepository.findById(senderAccountId)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));
        
        try {
            Long userId = senderAccount.getCustomer().getUser().getId();
            boolean isOtpValid = otpService.verifyOtp(userId, "TRANSFER", otp);
            if (!isOtpValid) {
                throw new RuntimeException("Invalid or expired OTP.");
            }
        } catch (Exception e) {
            throw new RuntimeException("OTP verification failed: " + e.getMessage());
        }

        Account receiverAccount = accountRepository.findById(receiverAccountId)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));
        
        if (senderAccount.getStatus() != AccountStatus.ACTIVE || receiverAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Both accounts must be active for transfers.");
        }
        if (senderAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds.");
        }

        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        String senderRemarks = (remarks == null || remarks.isEmpty()) ? "Transfer to account: " + receiverAccountId : remarks;
        Transaction withdrawal = createTransaction(senderAccount, amount, TransactionType.TRANSFER, senderRemarks);
        
        createTransaction(receiverAccount, amount, TransactionType.DEPOSIT, "Transfer from account: " + senderAccountId);

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
    public AccountResponse approveTermAccount(Long accountId, AccountApprovalRequest request) {
        // Find the account and ensure it is a TermAccount
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));

        if (!(account instanceof TermAccount)) {
            throw new IllegalArgumentException("This action is only applicable to Term Accounts.");
        }

        TermAccount termAccount = (TermAccount) account;

        // Set the properties from the admin's request
        termAccount.setStatus(AccountStatus.ACTIVE);
        termAccount.setInterestRate(request.getInterestRate());
        termAccount.setPenaltyAmount(request.getPenaltyAmount());
        
        Account updatedAccount = accountRepository.save(termAccount);
        
        return AccountMapper.toDTO(updatedAccount);
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

    // private double calculateInterest(Account account) {
    //     if (account instanceof SavingsAccount) {
    //         return account.getBalance() * (account.getInterestRate() / 100) / 12; // Monthly interest
    //     } else if (account instanceof TermAccount) {
    //         TermAccount termAccount = (TermAccount) account;
    //         return termAccount.getBalance() * (termAccount.getInterestRate() / 100) * termAccount.getMonths() / 12;
    //     }
    //     return 0;
    // }

    private Transaction createTransaction(Account account, double amount, 
                                    TransactionType type, String remarks) {
    // Make sure the account is managed (persisted)
    Account managedAccount = accountRepository.findById(account.getAccountId())
        .orElseThrow(() -> new RuntimeException("Account not found in database"));
    
    Transaction transaction = new Transaction();
    transaction.setAmount(amount);
    transaction.setTransactionType(type);
    transaction.setTransactionDateandTime(LocalDateTime.now());
    transaction.setBankAccount(managedAccount); // Use the managed entity
    transaction.setTransactionStatus(TransactionStatus.SUCCESS);
    transaction.setTransactionRemarks(remarks);
    
    return transactionRepository.save(transaction);
}
}