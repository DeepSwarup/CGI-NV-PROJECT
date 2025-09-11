package com.bank.bankApp.controllers;

import com.bank.bankApp.dtos.*;
import com.bank.bankApp.services.AccountService;
import com.bank.bankApp.services.IAccountService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private IAccountService accountService;

    @PostMapping("/savings")
    public ResponseEntity<AccountResponse> addSavingsAccount(
            HttpServletRequest request,
            @RequestBody SavingsAccountRequest savingsRequest) {
        Long userId = (Long) request.getAttribute("userId");
        AccountResponse accountResponse = accountService.addSavingsAccount(savingsRequest);
        return new ResponseEntity<>(accountResponse, HttpStatus.CREATED);
    }

    @PostMapping("/term")
    public ResponseEntity<AccountResponse> addTermAccount(
            HttpServletRequest request,
            @RequestBody TermAccountRequest termRequest) {
        Long userId = (Long) request.getAttribute("userId");
        AccountResponse accountResponse = accountService.addTermAccount(termRequest);
        return new ResponseEntity<>(accountResponse, HttpStatus.CREATED);
    }
     @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transferMoney(@RequestBody TransferRequest transferRequest) {
        // We pass empty strings for username/password as they are not used with JWT auth
        TransactionResponse transaction = accountService.transferMoney(
                transferRequest.getSenderAccountId(),
                transferRequest.getReceiverAccountId(),
                transferRequest.getAmount(), "", "");

        return ResponseEntity.ok(transaction);
    }


    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@RequestParam Long accountId, @RequestParam double amount) {
        return ResponseEntity.ok(accountService.withdraw(accountId, amount, "", ""));
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@RequestParam Long accountId, @RequestParam double amount) {
        return ResponseEntity.ok(accountService.deposit(accountId, amount));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> findAccountById(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.findAccountById(accountId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> viewAccounts(@PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.viewAccounts(customerId));
    }
    @GetMapping("/savings/{customerId}")
    public ResponseEntity<AccountResponse> viewSavingAcc(
            HttpServletRequest request,
            @PathVariable Long customerId) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(accountService.viewSavingAcc(customerId));
    }

    @GetMapping("/term/{customerId}")
    public ResponseEntity<AccountResponse> viewTermAcc(
            HttpServletRequest request,
            @PathVariable Long customerId) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(accountService.viewTermAcc(customerId));
    }

    @DeleteMapping("/savings/{accountId}")
    public ResponseEntity<String> closeSavingsAccount(
            HttpServletRequest request,
            @PathVariable Long accountId) {
        Long userId = (Long) request.getAttribute("userId");
        boolean success = accountService.closeSavingsAccount(accountId);
        if (success) {
            return ResponseEntity.ok("Savings account closed successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to close savings account");
    }

    @DeleteMapping("/term/{accountId}")
    public ResponseEntity<String> closeTermAccount(
            HttpServletRequest request,
            @PathVariable Long accountId) {
        Long userId = (Long) request.getAttribute("userId");
        boolean success = accountService.closeTermAccount(accountId);
        if (success) {
            return ResponseEntity.ok("Term account closed successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to close term account");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/compute-interest")
    public ResponseEntity<String> computeInterest(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        accountService.computeInterestForAllAccounts();
        return ResponseEntity.ok("Interest computed for all accounts");
    }
}