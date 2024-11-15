package com.example.demo.service.jwt;

import com.example.demo.enums.AppRole;
import com.example.demo.model.AppUser;
import com.example.demo.model.dto.*;
import com.example.demo.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthService {
    private final AppUserRepository appUserRepository;
//    private final PasswordEncoder passwordEncoder;

    private final JwtSecurityService jwtSecurityService;
    @Autowired
//    private final AuthenticationManager authenticationManager;

    public AuthService(AppUserRepository appUserRepository,
//                       PasswordEncoder passwordEncoder,
                       JwtSecurityService jwtSecurityService
//                       AuthenticationManager authenticationManager
    ) {
        this.appUserRepository = appUserRepository;
//        this.passwordEncoder = passwordEncoder;
        this.jwtSecurityService = jwtSecurityService;
//        this.authenticationManager = authenticationManager;
    }

    public RegisterResponseDto register (RegisterRequestDto registerRequestDto){
        AppUser appUser = new AppUser();
        appUser.setEmail(registerRequestDto.getEmail());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        appUser.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        appUser.setRole(AppRole.ROLE_USER);
        appUserRepository.save(appUser);

        String token = jwtSecurityService.generateToken(appUser);
        String refreshToken = jwtSecurityService.generateRefreshToken(new HashMap<>(), appUser);

        return RegisterResponseDto
                .builder()
                .email(registerRequestDto.getEmail())
                .jwtToken(token)
                .refreshToken(refreshToken)
                .build();
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto){

        AppUser user = appUserRepository
                .findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Ошибка"));

        String token = jwtSecurityService.generateToken(user);
        String refreshToken = jwtSecurityService.generateRefreshToken(new HashMap<>(), user);

        return LoginResponseDto
                .builder()
                .email(loginRequestDto.getEmail())
                .jwtToken(token)
                .refreshToken(refreshToken)
                .build();
    }

    public RefreshTokenResponseDto refresh (RefreshTokenRequestDto refreshTokenRequestDto){
        String jwt = refreshTokenRequestDto.getRefreshToken();
        String email = jwtSecurityService.extractUsername(jwt);
        AppUser user = appUserRepository
                .findByEmail(email)
                .orElseThrow();
        if(jwtSecurityService.validateToken(jwt, user)){
            RefreshTokenResponseDto refreshTokenResponseDto = new RefreshTokenResponseDto();

            refreshTokenResponseDto
                    .setJwtToken(jwtSecurityService.generateToken(user));

            refreshTokenResponseDto
                    .setRefreshToken(jwtSecurityService.generateRefreshToken(new HashMap<>(), user));

            return refreshTokenResponseDto;
        }
        return null;
    }
}
