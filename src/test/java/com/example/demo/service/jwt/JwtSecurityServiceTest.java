package com.example.demo.service.jwt;

import com.example.demo.enums.AppRole;
import com.example.demo.model.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Method;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
public class JwtSecurityServiceTest {

    private JwtSecurityService jwtSecurityService;

    @Mock
    private AppUser mockUserDetails;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        jwtSecurityService = new JwtSecurityService();
    }

    @Test
    public void testGenerateToken_WithCustomUserDetails() {
        when(mockUserDetails.getUsername()).thenReturn("test@example.com");
        when(mockUserDetails.getRole()).thenReturn(AppRole.ROLE_USER);

        String token = jwtSecurityService.generateToken(mockUserDetails);

        assertNotNull(token);
    }

    @Test
    public void testExtractUsername() {
        when(mockUserDetails.getUsername()).thenReturn("test@example.com");
        String token = jwtSecurityService.generateToken(mockUserDetails);

        String extractedUsername = jwtSecurityService.extractUsername(token);

        assertEquals("test@example.com", extractedUsername);
    }

    @Test
    public void testExtractExpiration() {
        when(mockUserDetails.getUsername()).thenReturn("test@example.com");
        String token = jwtSecurityService.generateToken(mockUserDetails);

        Date expirationDate = jwtSecurityService.extractExpiration(token);

        assertAll(
                () -> assertNotNull(expirationDate),
                () -> assertTrue(expirationDate.after(new Date()))
        );

    }

    @Test
    public void testIsTokenExpired_NonExpiredToken() {
        when(mockUserDetails.getUsername()).thenReturn("test@example.com");
        String token = jwtSecurityService.generateToken(mockUserDetails);

        boolean isExpired = jwtSecurityService.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    public void testValidateToken_ValidToken() {
        when(mockUserDetails.getUsername()).thenReturn("test@example.com");
        String token = jwtSecurityService.generateToken(mockUserDetails);

        boolean isValid = jwtSecurityService.validateToken(token, mockUserDetails);

        assertTrue(isValid);
    }

    @Test
    public void testValidateToken_InvalidUsername() {
        when(mockUserDetails.getUsername()).thenReturn("test@example.com");
        String token = jwtSecurityService.generateToken(mockUserDetails);

        UserDetails differentUser = mock(UserDetails.class);
        when(differentUser.getUsername()).thenReturn("different@example.com");

        boolean isValid = jwtSecurityService.validateToken(token, differentUser);

        assertFalse(isValid);
    }

    @Test
    public void testGenerateToken_WithCustomClaims() {
        when(mockUserDetails.getUsername()).thenReturn("admin@example.com");
        when(mockUserDetails.getRole()).thenReturn(AppRole.ROLE_USER);

        String token = jwtSecurityService.generateToken(mockUserDetails);

        assertNotNull(token);

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode("5Hdo5+PxMJkLQ9Wo7WnYMR/gBzTfC5XrB3iNPvMlscY=")))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("admin@example.com", claims.getSubject());
        assertEquals(AppRole.ROLE_USER.name(), claims.get("role")); // Assuming the role is added in generateToken method
    }

    @Test
    void generateToken_WithAppUser_SuccessfulTokenGeneration() {
        when(mockUserDetails.getUsername()).thenReturn("testuser@example.com");
        when(mockUserDetails.getRole()).thenReturn(AppRole.ROLE_USER);

        String token = jwtSecurityService.generateToken(mockUserDetails);

        assertAll(
                () ->  assertNotNull(token),
                () -> assertFalse(token.isEmpty())
        );

        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKeyViaReflection())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertAll(
                () -> assertEquals("testuser@example.com", claims.getSubject()),
                () -> assertEquals(AppRole.ROLE_USER.toString(), claims.get("role"))
        );

        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();
        long differenceInHours = (expiration.getTime() - issuedAt.getTime()) / 60000;

        assertAll(
                () -> assertNotNull(issuedAt),
                () -> assertNotNull(expiration),
                () -> assertTrue(differenceInHours >= 23 && differenceInHours <= 25)
        );

    }

    @Test
    void generateToken_WithNonAppUser_SuccessfulTokenGeneration() {
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("testuser@example.com");

        String token = jwtSecurityService.generateToken(mockUserDetails);

        assertAll(
                () ->  assertNotNull(token),
                () -> assertFalse(token.isEmpty())
        );

        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKeyViaReflection())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertAll(
                () -> assertEquals("testuser@example.com", claims.getSubject()),
                () -> assertNull(claims.get("role"))
        );
    }

    private Key getSigningKeyViaReflection() {
        try {
            JwtSecurityService service = new JwtSecurityService();
            Method signingKeyMethod = JwtSecurityService.class.getDeclaredMethod("getSigningKey");
            signingKeyMethod.setAccessible(true);
            return (Key) signingKeyMethod.invoke(service);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить ключ", e);
        }
    }

}