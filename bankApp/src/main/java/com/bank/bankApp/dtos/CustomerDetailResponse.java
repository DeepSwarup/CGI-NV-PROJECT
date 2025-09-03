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
public class CustomerDetailResponse {
    private Long customerId;
    private String phoneNo;
    private int age;
    private String gender;
    private String userName;
    private String userEmail;

}