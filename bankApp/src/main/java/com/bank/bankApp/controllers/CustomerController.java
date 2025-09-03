package com.bank.bankApp.controllers;

import com.bank.bankApp.dtos.CustomerDetailResponse;
import com.bank.bankApp.dtos.CustomerRequest;
import com.bank.bankApp.dtos.CustomerResponse;
import com.bank.bankApp.services.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> addCustomer(
            HttpServletRequest request,
            @RequestBody CustomerRequest customerRequest
            ){
        Long userId = (Long) request.getAttribute("userId");
        return new ResponseEntity<>(customerService.addCustomer(userId, customerRequest), HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity<CustomerResponse> getCustomer(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(customerService.getCustomer(userId));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Boolean>> deleteCustomer(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        customerService.deleteCustomer(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<CustomerResponse> updateCustomer(HttpServletRequest request,
                                                           @RequestBody CustomerRequest customerRequest) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(customerService.updateCustomer(userId, customerRequest));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers(HttpServletRequest request) {
        String role = (String) request.getAttribute("role"); // extracted in your JWT filter
        if (!"CUSTOMER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDetailResponse> getCustomerById(@PathVariable("id") Long id,
                                                                  HttpServletRequest request) {
        String role = (String) request.getAttribute("role"); // extracted from JWT
        if (!"CUSTOMER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        CustomerDetailResponse response = customerService.getCustomerById(id);
        return ResponseEntity.ok(response);
    }


}
