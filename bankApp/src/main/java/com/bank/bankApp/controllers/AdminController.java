package com.bank.bankApp.controllers;

import com.bank.bankApp.dtos.*;
import com.bank.bankApp.entity.User;
import com.bank.bankApp.enums.AccountStatus;
import com.bank.bankApp.enums.TransactionType;
import com.bank.bankApp.services.AdminService;
import com.bank.bankApp.services.CustomerService;
import com.bank.bankApp.services.IAccountService;
import com.bank.bankApp.services.TransactionService;
import com.bank.bankApp.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/admin-check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminCheck(){
        return new ResponseEntity<>("I am in amdin controller", HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminResponse> addCustomer(
            HttpServletRequest request,
            @RequestBody AdminRequest adminRequest
    ){
        Long userId = (Long) request.getAttribute("userId");
        return new ResponseEntity<>(adminService.addAdmin(userId, adminRequest), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdmin(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(adminService.getAdmin(userId));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> deleteAdmin(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        adminService.deleteAdmin(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminResponse> updateAdmin(HttpServletRequest request,
                                                           @RequestBody AdminRequest adminRequest) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(adminService.updateAdmin(userId, adminRequest));
    }

    
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(adminService.listAllAccounts());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminResponse>> getAllAdmins(HttpServletRequest request) {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDetailResponse> getAdminById(@PathVariable("id") Long id,
                                                                  HttpServletRequest request) {

        AdminDetailResponse response = adminService.getAdminById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accounts/{accountId}/approve")
    public ResponseEntity<AccountResponse> approveSavingsAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.updateAccountStatus(accountId, AccountStatus.ACTIVE));
    }

     @PostMapping("/accounts/term/{accountId}/approve")
    public ResponseEntity<AccountResponse> approveTermAccount(@PathVariable Long accountId, @RequestBody AccountApprovalRequest request) {
        return ResponseEntity.ok(accountService.approveTermAccount(accountId, request));
    }

    @PostMapping("/accounts/{accountId}/credit-interest")
    public ResponseEntity<TransactionResponse> creditInterest(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.creditInterest(accountId));
    }

    @GetMapping("/transactions/interest-log")
    public ResponseEntity<Set<TransactionResponse>> getInterestTransactionLogs() {
        return ResponseEntity.ok(transactionService.findAllByTransactionType(TransactionType.DEPOSIT));
    }


      @PostMapping("/accounts/{accountId}/decline")
    public ResponseEntity<AccountResponse> declineAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.updateAccountStatus(accountId, AccountStatus.DECLINED));
    }

    @PostMapping("/accounts/{accountId}/interest-rate")
    public ResponseEntity<AccountResponse> updateInterestRate(
            @PathVariable Long accountId, 
            @RequestBody Map<String, Double> payload) {
        Double newInterestRate = payload.get("interestRate");
        if (newInterestRate == null || newInterestRate < 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(accountService.updateInterestRate(accountId, newInterestRate));
    }

}
