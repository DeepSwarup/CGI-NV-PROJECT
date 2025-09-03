package com.bank.bankApp.mapper;

import com.bank.bankApp.dtos.CustomerRequest;
import com.bank.bankApp.dtos.CustomerResponse;
import com.bank.bankApp.entity.Customer;

public class CustomerMapper {

    public static CustomerResponse toDTO(Customer customer) {
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .phoneNo(customer.getPhoneNo())
                .age(customer.getAge())
                .gender(customer.getGender())
                .email(customer.getUser().getEmail()) // safe to expose
                .build();
    }

    public static Customer toEntity(CustomerRequest dto) {
        Customer customer = new Customer();
        customer.setPhoneNo(dto.getPhoneNo());
        customer.setAge(dto.getAge());
        customer.setGender(dto.getGender());
        return customer;
    }
}
