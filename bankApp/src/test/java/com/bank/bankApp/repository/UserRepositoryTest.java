package com.bank.bankApp.repository;

import com.bank.bankApp.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindUser(){

        Optional<User> findUser = userRepository.findByEmail("test@gmail.com");

        Assertions.assertTrue(findUser.isPresent());
        Assertions.assertEquals("test@gmail.com", findUser.get().getEmail());

    }
}
