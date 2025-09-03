package com.bank.bankApp.mapper;

import com.bank.bankApp.dtos.AdminRequest;
import com.bank.bankApp.dtos.AdminResponse;
import com.bank.bankApp.dtos.CustomerRequest;
import com.bank.bankApp.dtos.CustomerResponse;
import com.bank.bankApp.entity.Admin;
import com.bank.bankApp.entity.Customer;

public class AdminMapper {

    public static AdminResponse toDTO(Admin admin) {
        return AdminResponse.builder()
                .adminId(admin.getAdminId())
                .contact(admin.getContact())
                .age(admin.getAge())
                .gender(admin.getGender())
                .email(admin.getUser().getEmail()) // safe to expose
                .build();
    }

    public static Admin toEntity(AdminRequest dto) {

        return Admin.builder()
                .contact(dto.getContact())
                .age(dto.getAge())
                .gender(dto.getGender())
                .build();
    }
}
