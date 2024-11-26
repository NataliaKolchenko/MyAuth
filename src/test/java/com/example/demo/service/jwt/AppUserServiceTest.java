package com.example.demo.service.jwt;

import com.example.demo.model.AppUser;
import com.example.demo.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {
    @Mock
    private AppUserRepository appUserRepository;

    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        openMocks(this);
        userDetailsService = new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                AppUser user = appUserRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
                return user;
            }
        };
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        String email = "test@example.com";
        AppUser mockUser = mock(AppUser.class);
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

      assertAll(
              () -> assertNotNull(userDetails),
              () -> assertEquals(mockUser, userDetails),

              () -> verify(appUserRepository).findByEmail(email)
      );

    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String email = "nonexistent@example.com";
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertAll(
                () -> assertThrows(UsernameNotFoundException.class, () -> {
                    userDetailsService.loadUserByUsername(email);
                }),
                () -> verify(appUserRepository).findByEmail(email)
        );
    }

}