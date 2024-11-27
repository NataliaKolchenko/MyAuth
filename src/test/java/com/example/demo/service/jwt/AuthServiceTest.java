package com.example.demo.service.jwt;

import com.example.demo.enums.AppRole;
import com.example.demo.model.AppUser;
import com.example.demo.model.dto.LoginRequestDto;
import com.example.demo.model.dto.LoginResponseDto;
import com.example.demo.model.dto.RegisterRequestDto;
import com.example.demo.model.dto.RegisterResponseDto;
import com.example.demo.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private JwtSecurityService jwtSecurityService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_Successful() {
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123");

        String jwtToken = "generatedToken";

        when(jwtSecurityService.generateToken(any(AppUser.class))).thenReturn(jwtToken);

        RegisterResponseDto responseDto = authService.register(requestDto);

        assertAll(
                () -> assertNotNull(responseDto),
                () -> assertEquals(requestDto.getEmail(), responseDto.getEmail()),
                () -> assertEquals(jwtToken, responseDto.getJwtToken()),

                () -> verify(appUserRepository).save(argThat(user -> {
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    assertAll(
                            () -> assertNotNull(user),
                            () -> assertEquals(requestDto.getEmail(), user.getEmail()),
                            () -> assertEquals(AppRole.ROLE_USER, user.getRole()),
                            () -> assertTrue(passwordEncoder.matches(requestDto.getPassword(), user.getPassword()))

                    );
                    return true;
                })),

                () -> verify(jwtSecurityService).generateToken(any(AppUser.class))
        );

    }


    @Test
    void register_TokenGenerationFailure() {
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123");

        when(jwtSecurityService.generateToken(any(AppUser.class))).thenThrow(new RuntimeException("Token generation failed"));

        assertAll(
                () -> assertThrows(RuntimeException.class, () -> {
                    authService.register(requestDto);
                }),
                () -> verify(appUserRepository).save(any(AppUser.class))
        );
    }


    @Test
    void testLogin_Successful() {
        String token = "jwtToken";

        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123");

        AppUser mockUser = new AppUser();
        mockUser.setEmail(requestDto.getEmail());
        mockUser.setPassword(new BCryptPasswordEncoder().encode(requestDto.getPassword()));
        mockUser.setRole(AppRole.ROLE_USER);

        when(appUserRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(mockUser));

        when(jwtSecurityService.generateToken(mockUser)).thenReturn(token);

        LoginResponseDto responseDto = authService.login(requestDto);

        assertAll(
                () -> assertNotNull(responseDto),
                () -> assertEquals(requestDto.getEmail(), responseDto.getEmail()),
                () -> assertEquals(token, responseDto.getJwtToken()),

                () -> verify(appUserRepository).findByEmail(requestDto.getEmail()),
                () -> verify(jwtSecurityService).generateToken(mockUser)
        );

    }

    @Test
    void testLogin_UserNotFound_Exception() {
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setEmail("nonexistent@example.com");
        requestDto.setPassword("password123");

        when(appUserRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.login(requestDto);
        });
        assertAll(
                () -> assertEquals("Ошибка Аутентификации", exception.getMessage()),
                () -> verifyNoInteractions(jwtSecurityService)
        );
    }
}