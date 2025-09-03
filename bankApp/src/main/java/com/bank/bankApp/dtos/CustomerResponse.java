package com.bank.bankApp.dtos;


import com.bank.bankApp.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {
    private Long customerId;
    private String phoneNo;
    private int age;
    private Gender gender;
    private String email;
}