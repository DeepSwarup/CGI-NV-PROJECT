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
    public ResponseEntity<TransactionResponse> transferMoney(
            HttpServletRequest request,
            @RequestParam Long senderAccountId,
            @RequestParam Long receiverAccountId,
            @RequestParam double amount) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(accountService.transferMoney(
                senderAccountId, receiverAccountId, amount, "", ""));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            HttpServletRequest request,
            @RequestParam Long accountId,
            @RequestParam double amount) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(accountService.withdraw(accountId, amount, "", ""));
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            HttpServletRequest request,
            @RequestParam Long accountId,
            @RequestParam double amount) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(accountService.deposit(accountId, amount));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> findAccountById(
            HttpServletRequest request,
            @PathVariable Long accountId) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(accountService.findAccountById(accountId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Set<AccountResponse>> viewAccounts(
            HttpServletRequest request,
            @PathVariable Long customerId) {
        Long userId = (Long) request.getAttribute("userId");
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