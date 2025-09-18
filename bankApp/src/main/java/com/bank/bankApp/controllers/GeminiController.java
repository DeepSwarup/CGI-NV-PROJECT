package com.bank.bankApp.controllers;

import com.bank.bankApp.dtos.TransactionResponse;
import com.bank.bankApp.services.GeminiService;
import com.bank.bankApp.services.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/ai")
public class GeminiController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private GeminiService geminiService;

    @GetMapping("/summary/weekly")
    public ResponseEntity<Map<String, String>> getWeeklySummary(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);

        // Fetch all transactions for the user across all their accounts in the last 7 days
        Set<TransactionResponse> allTransactions = transactionService.getAllUserTransactionsBetweenDates(userId, sevenDaysAgo, today);

        // Get the summary from the Gemini Service
        String summary = geminiService.getTransactionSummary(allTransactions);

        // Return the summary in a simple JSON object
        return ResponseEntity.ok(Map.of("summary", summary));
    }
}
