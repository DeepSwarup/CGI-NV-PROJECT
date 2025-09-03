package com.bank.bankApp.dtos;

import com.bank.bankApp.enums.Gender;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminRequest {
    private String contact;
    private int age;
    private Gender gender;
}
