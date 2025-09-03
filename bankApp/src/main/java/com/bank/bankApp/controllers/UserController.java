package com.bank.bankApp.controllers;

import com.bank.bankApp.dtos.AuthResponse;
import com.bank.bankApp.dtos.LoginRequest;
import com.bank.bankApp.dtos.SignUpRequest;
import com.bank.bankApp.entity.User;
import com.bank.bankApp.services.AuthService;
import com.bank.bankApp.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@Tag(name = "User Controller", description = "Handles user operations")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest request){
        User user = userService.saveNewUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
//        System.out.println("Email: " + request.getEmail() + ", Password: " + request.getPassword());
        String token = authService.login(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(new AuthResponse(token), HttpStatus.OK);
    }


}
