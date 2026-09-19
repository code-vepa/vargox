package com.codevepa.vargox.controller;

import com.codevepa.vargox.entities.User;
import com.codevepa.vargox.model.LoginRequest;
import com.codevepa.vargox.model.LoginResponse;
import com.codevepa.vargox.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User newUser = userService.registerUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest);
        return ResponseEntity.ok(new LoginResponse(user.getUsername(), user.getRole().name()));
    }
}