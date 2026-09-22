package com.codevepa.vargox.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JwtServiceTest {
    private JwtService jwtService;

    // 32 bytes string
    private static final String TEST_SECRET =
            "thisIsATestSecretKeyThatIsLongEnoughForHmacSha256AndHS512Algorithms";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L); // 1 hour

    }
    
    // test for generateToken

    @Test
    void generateToken_shouldReturnNonNullNonBlankToken() {
        String token = jwtService.generateToken("arlan", "USER");

        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
    }

    @Test
    void generateToken_shouldProduceTokenWithThreeParts() {
        // JWTs are structured as header.payload.signature
        String token = jwtService.generateToken("arlan", "USER");

        String[] parts = token.split("\\."); // split by delimiter
        assertThat(parts).hasSize(3);
    }

    // test for extractUsername
    
    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtService.generateToken("arlan", "USER");

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("arlan");
    }

    // test for extractRole

    @Test
    void extractRole_shouldReturnCorrectRole() {
        String token = jwtService.generateToken("admin", "ADMIN");

        String role = jwtService.extractRole(token);

        assertThat(role).isEqualTo("ADMIN");
    }

    // test for isTokenValid

    @Test
    void isTokenValid_shouldReturnTrue_whenUsernameMatchesAndNotExpired() {
        String token = jwtService.generateToken("arlan", "USER");

        boolean valid = jwtService.isTokenValid(token, "arlan");

        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenUsernameDoesNotMatch() {
        String token = jwtService.generateToken("arlan", "USER");

        boolean valid = jwtService.isTokenValid(token, "someoneElse");

        assertThat(valid).isFalse();
    }

    @Test
    void isTokenValid_shouldThrow_whenTokenIsExpired() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L); // already expired
        String expiredToken = jwtService.generateToken("arlan", "USER");

        // jjwt throws ExpiredJwtException when parsing an expired token,
        // rather than returning false silently
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, "arlan"));
    }

    @Test
    void isTokenValid_shouldThrow_whenUsernameDoesNotMatchAndTokenIsExpired() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L); // already expired
        String expiredToken = jwtService.generateToken("arlan", "USER");

        // extractUsername() runs first inside isTokenValid, and parsing an
        // expired token throws before the username comparison ever happens
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, "someoneElse"));
    }

    // test for malformed tokens

    @Test
    void extractUsername_shouldThrow_whenTokenIsTamperedWith() {
        String token = jwtService.generateToken("arlan", "USER");
        String tamperedToken = token.substring(0, token.length() - 2) + "xx"; // corrupt the signature

        assertThrows(io.jsonwebtoken.security.SignatureException.class,
                () -> jwtService.extractUsername(tamperedToken));
    }

    @Test
    void extractUsername_shouldThrow_whenTokenIsMalformed() {
        assertThrows(io.jsonwebtoken.MalformedJwtException.class,
                () -> jwtService.extractUsername("not.a.real.token")); // 4 parts instead of 3
    }

    @Test
    void extractUsername_shouldThrow_whenSignedWithDifferentSecret() {
        String token = jwtService.generateToken("arlan", "USER");

        JwtService otherService = new JwtService();
        ReflectionTestUtils.setField(otherService, "secret",
                "aCompletelyDifferentSecretKeyThatWontMatchTheOriginalOneButIsStillLongEnoughForHS512Signing");
        ReflectionTestUtils.setField(otherService, "expiration", 3600000L);

        // token was signed with a different (valid-length) key, so verification must fail
        assertThrows(io.jsonwebtoken.security.SignatureException.class,
                () -> otherService.extractUsername(token));
    }
}