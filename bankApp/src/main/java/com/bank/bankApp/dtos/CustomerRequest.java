package com.bank.bankApp.dtos;


import com.bank.bankApp.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest {
    private String phoneNo;
    private int age;
    private Gender gender;
}