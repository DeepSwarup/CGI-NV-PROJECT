package com.bank.bankApp.controllers;

import com.bank.bankApp.dtos.*;
import com.bank.bankApp.entity.User;
import com.bank.bankApp.services.AdminService;
import com.bank.bankApp.services.CustomerService;
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

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

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

}
