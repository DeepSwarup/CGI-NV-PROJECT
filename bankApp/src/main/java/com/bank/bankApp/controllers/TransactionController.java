package com.bank.bankApp.controllers;

import com.bank.bankApp.dtos.TransactionRequest;
import com.bank.bankApp.dtos.TransactionResponse;
import com.bank.bankApp.services.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(HttpServletRequest request,
                                                                 @RequestBody TransactionRequest dto) {
        Long userId = (Long) request.getAttribute("userId");
        TransactionResponse savedTransaction = transactionService.createTransaction(userId, dto);
        return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
    }

    
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> viewTransaction(HttpServletRequest request,
                                                               @PathVariable long transactionId) {
        Long userId = (Long) request.getAttribute("userId");
        TransactionResponse transaction = transactionService.viewTransaction(userId, transactionId);
        return ResponseEntity.ok(transaction);
    }

   
    @GetMapping("/find/{transactionId}")
    public ResponseEntity<TransactionResponse> findTransactionById(HttpServletRequest request,
                                                                   @PathVariable long transactionId) {
        Long userId = (Long) request.getAttribute("userId");
        TransactionResponse transaction = transactionService.findTransactionById(userId, transactionId);
        return ResponseEntity.ok(transaction);
    }

  
    @GetMapping("/account/{accountId}")
    public ResponseEntity<Set<TransactionResponse>> listAllTransactions(HttpServletRequest request,
                                                                        @PathVariable long accountId,
                                                                        @RequestParam LocalDate from,
                                                                        @RequestParam LocalDate to) {
        Long userId = (Long) request.getAttribute("userId");
        Set<TransactionResponse> transactions =
                transactionService.listAllTransactions(userId, accountId, from, to);
        return ResponseEntity.ok(transactions);
    }

    
    @GetMapping("/account/{accountId}/all")
    public ResponseEntity<Set<TransactionResponse>> getAllMyAccTransactions(HttpServletRequest request,
                                                                            @PathVariable long accountId) {
        Long userId = (Long) request.getAttribute("userId");
        Set<TransactionResponse> transactions =
                transactionService.getAllMyAccTransactions(userId, accountId);
        return ResponseEntity.ok(transactions);
    }
}