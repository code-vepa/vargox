package com.codevepa.vargox.service;

import com.codevepa.vargox.entities.User;
import com.codevepa.vargox.enums.Role;
import com.codevepa.vargox.model.LoginRequest;
import com.codevepa.vargox.model.LoginResponse;
import com.codevepa.vargox.repository.UserRepo;
import com.codevepa.vargox.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User("arlan", "encodedPassword123", Role.USER);
    }

    // test for registerUser

    @Test
    void registerUser_shouldSaveWithHashedPasswordAndUserRole_whenValid() {
        User input = new User("arlan", "plainPassword", Role.ADMIN); // attempt to self-assign ADMIN

        when(userRepo.existsByUsername("arlan")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerUser(input);

        assertThat(result.getRole()).isEqualTo(Role.USER); // forced, not ADMIN
        assertThat(result.getPassword()).isEqualTo("hashedPassword"); // hashed, not plain
        verify(userRepo).save(input);
    }

    @Test
    void registerUser_shouldThrow_whenUsernameIsNull() {
        User input = new User(null, "password123", Role.USER);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(input));
        verify(userRepo, never()).save(any());
    }

    @Test
    void registerUser_shouldThrow_whenUsernameIsBlank() {
        User input = new User(" ", "password123", Role.USER);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(input));
        verify(userRepo, never()).save(any());
    }

    @Test
    void registerUser_shouldThrow_whenPasswordIsNull() {
        User input = new User("arlan", null, Role.USER);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(input));
        verify(userRepo, never()).save(any());
    }

    @Test
    void registerUser_shouldThrow_whenPasswordIsBlank() {
        User input = new User("arlan", " ", Role.USER);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(input));
        verify(userRepo, never()).save(any());
    }

    @Test
    void registerUser_shouldThrow_whenUsernameAlreadyTaken() {
        User input = new User("arlan", "password123", Role.USER);
        when(userRepo.existsByUsername("arlan")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(input)
        );

        assertThat(exception.getMessage()).contains("arlan");
        verify(userRepo, never()).save(any());
    }

    // test for login

    @Test
    void login_shouldReturnTokenAndUserInfo_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("arlan");
        request.setPassword("plainPassword");

        when(userRepo.findByUsername("arlan")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("plainPassword", "encodedPassword123")).thenReturn(true);
        when(jwtService.generateToken("arlan", "USER")).thenReturn("fake.jwt.token");

        LoginResponse result = userService.login(request);

        assertThat(result.getUsername()).isEqualTo("arlan");
        assertThat(result.getRole()).isEqualTo("USER");
        assertThat(result.getToken()).isEqualTo("fake.jwt.token");
    }

    @Test
    void login_shouldThrowBadRequest_whenUsernameIsNull() {
        LoginRequest request = new LoginRequest();
        request.setUsername(null);
        request.setPassword("password123");

        assertThrows(IllegalArgumentException.class, () -> userService.login(request));
        verify(userRepo, never()).findByUsername(any());
    }

    @Test
    void login_shouldThrowBadRequest_whenUsernameIsBlank() {
        LoginRequest request = new LoginRequest();
        request.setUsername(" ");
        request.setPassword("password123");

        assertThrows(IllegalArgumentException.class, () -> userService.login(request));
        verify(userRepo, never()).findByUsername(any());
    }

    @Test
    void login_shouldThrowBadRequest_whenPasswordIsNull() {
        LoginRequest request = new LoginRequest();
        request.setUsername("arlan");
        request.setPassword(null);

        assertThrows(IllegalArgumentException.class, () -> userService.login(request));
        verify(userRepo, never()).findByUsername(any());
    }

    @Test
    void login_shouldThrowBadRequest_whenPasswordIsBlank() {
        LoginRequest request = new LoginRequest();
        request.setUsername("arlan");
        request.setPassword(" ");

        assertThrows(IllegalArgumentException.class, () -> userService.login(request));
        verify(userRepo, never()).findByUsername(any());
    }

    @Test
    void login_shouldThrowUnauthorized_whenUsernameDoesNotExist() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password123");

        when(userRepo.findByUsername("nonexistent")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.login(request)
        );
        assertThat(exception.getStatusCode().value()).isEqualTo(401);
    }

    @Test
    void login_shouldThrowUnauthorized_whenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest();
        request.setUsername("arlan");
        request.setPassword("wrongPassword");

        when(userRepo.findByUsername("arlan")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword123")).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.login(request)
        );
        assertThat(exception.getStatusCode().value()).isEqualTo(401);
        verify(jwtService, never()).generateToken(any(), any());
    }

    @Test
    void login_shouldReturnSameErrorMessage_forWrongUsernameAndWrongPassword() {
        // wrong username case
        LoginRequest wrongUsername = new LoginRequest();
        wrongUsername.setUsername("nonexistent");
        wrongUsername.setPassword("password123");
        when(userRepo.findByUsername("nonexistent")).thenReturn(Optional.empty());

        ResponseStatusException ex1 = assertThrows(ResponseStatusException.class, () -> userService.login(wrongUsername));

        // wrong password case
        LoginRequest wrongPassword = new LoginRequest();
        wrongPassword.setUsername("arlan");
        wrongPassword.setPassword("wrongPassword");
        when(userRepo.findByUsername("arlan")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword123")).thenReturn(false);

        ResponseStatusException ex2 = assertThrows(ResponseStatusException.class, () -> userService.login(wrongPassword));

        // both must be identical — prevents user enumeration
        assertThat(ex1.getReason()).isEqualTo(ex2.getReason());
        assertThat(ex1.getStatusCode()).isEqualTo(ex2.getStatusCode());
    }
}
