package com.bank.bankApp.services;

import com.bank.bankApp.dtos.SignUpRequest;
import com.bank.bankApp.entity.User;
import com.bank.bankApp.enums.Role;
import com.bank.bankApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User saveNewUser(SignUpRequest request){
        try{
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
//            user.setRole(Role.ADMIN);
            user.setRole(Role.CUSTOMER);
            return userRepository.save(user);
        } catch (Exception e) {
            throw new IllegalArgumentException("Email already exist!",e);
        }
    }

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public User getUserbyId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
}
