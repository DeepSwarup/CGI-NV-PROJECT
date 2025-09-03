package com.bank.bankApp.services;

import com.bank.bankApp.dtos.LoginRequest;
import com.bank.bankApp.entity.User;
import com.bank.bankApp.repository.UserRepository;
import com.bank.bankApp.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    public String login(String email, String password){
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        authenticationManager.authenticate(authToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jwtUtil.generateToken(user.getId(),user.getName(), user.getEmail(), user.getRole().name());
    }

}
