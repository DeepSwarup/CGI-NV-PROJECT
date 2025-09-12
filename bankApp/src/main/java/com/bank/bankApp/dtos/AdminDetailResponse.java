package com.bank.bankApp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // This is the one the compiler is currently finding
@Builder
public class AdminDetailResponse {
    private Long customerId; // Note: In your code this is for an Admin, might want to rename to adminId
    private String contact;
    private int age;
    private String gender;
    private String userName;
    private String userEmail;

    /**
     * Manually added constructor to fix the compile error.
     * This constructor matches the arguments being passed in AdminService.
     */
    public AdminDetailResponse(Long customerId, String contact, int age, String gender, String userName, String userEmail) {
        this.customerId = customerId;
        this.contact = contact;
        this.age = age;
        this.gender = gender;
        this.userName = userName;
        this.userEmail = userEmail;
    }
}
