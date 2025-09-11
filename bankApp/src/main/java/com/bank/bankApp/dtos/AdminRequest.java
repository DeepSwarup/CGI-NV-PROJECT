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
    
    // --- MANUAL GETTERS AND SETTERS TO FIX COMPILE ERRORS ---

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
