package com.bank.bankApp.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDetailResponse {
    private Long customerId;
    private String contact;
    private int age;
    private String gender;
    private String userName;
    private String userEmail;

}