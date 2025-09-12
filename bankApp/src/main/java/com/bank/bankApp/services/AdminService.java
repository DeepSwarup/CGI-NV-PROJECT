package com.bank.bankApp.services;

import com.bank.bankApp.dtos.*;
import com.bank.bankApp.entity.Admin;
import com.bank.bankApp.entity.Customer;
import com.bank.bankApp.entity.Account;
import com.bank.bankApp.mapper.AccountMapper;
import com.bank.bankApp.repository.AccountRepository;
import com.bank.bankApp.entity.User;
import com.bank.bankApp.mapper.AdminMapper;
import com.bank.bankApp.mapper.CustomerMapper;
import com.bank.bankApp.repository.AdminRepository;
import com.bank.bankApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;



    public List<AccountResponse> listAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(AccountMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public AdminResponse addAdmin(Long userId, AdminRequest request){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));

        Admin admin = AdminMapper.toEntity(request);
        admin.setUser(user);
        return AdminMapper.toDTO(adminRepository.save(admin));

    }

    public AdminResponse getAdmin(Long userId){
        Admin admin = adminRepository.findByUser_Id(userId).orElseThrow(()-> new RuntimeException("Admin not found with userId "+userId));
        return AdminMapper.toDTO(admin);
    }

    public void deleteAdmin(Long userId) {
        Admin admin = adminRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found for userId " + userId));

        adminRepository.delete(admin);
    }

    public AdminResponse updateAdmin(Long userId, AdminRequest request) {
        Admin admin = adminRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Admin not found for userId " + userId));


        if (request.getContact() != null && !request.getContact().isEmpty()) {
            admin.setContact(request.getContact());
        }
        if (request.getAge() != 0) {
            admin.setAge(request.getAge());
        }
        if (request.getGender() != null) {
            admin.setGender(request.getGender());
        }

        return AdminMapper.toDTO(adminRepository.save(admin));
    }

    public List<AdminResponse> getAllAdmins() {
        return adminRepository.findAll()
                .stream()
                .map(AdminMapper::toDTO)
                .toList();
    }

    public AdminDetailResponse getAdminById(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + adminId));

        User user = admin.getUser();

        return new AdminDetailResponse(
                admin.getAdminId(),
                admin.getContact(),
                admin.getAge(),
                admin.getGender() != null ? admin.getGender().name() : null,
                user.getName(),
                user.getEmail()
        );
    }


}
