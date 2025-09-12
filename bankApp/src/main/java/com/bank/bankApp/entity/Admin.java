package com.bank.bankApp.entity;

import com.bank.bankApp.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter // Keeping lombok annotations is still good practice
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    @Column(nullable = false, unique = false)
    private String contact;

    private int age;

    private Gender gender;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    // --- MANUAL GETTERS AND SETTERS TO FIX COMPILE ERRORS ---

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
