package com.codevepa.vargox.service;

import com.codevepa.vargox.entities.User;
import com.codevepa.vargox.enums.Role;
import com.codevepa.vargox.repository.UserRepo;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User registerUser(User user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (userRepo.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username " + user.getUsername() + " is already taken");
        }

        user.setRole(Role.USER);
        return userRepo.save(user);
    }
}