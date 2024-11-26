package com.example.demo.service.jwt;

import com.example.demo.enums.AppRole;
import com.example.demo.model.AppUser;
import com.example.demo.model.dto.*;
import com.example.demo.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    private final AppUserRepository appUserRepository;

    private final JwtSecurityService jwtSecurityService;
    @Autowired
    public AuthService(AppUserRepository appUserRepository,
                       JwtSecurityService jwtSecurityService
    ) {
        this.appUserRepository = appUserRepository;
        this.jwtSecurityService = jwtSecurityService;
    }

    public RegisterResponseDto register (RegisterRequestDto registerRequestDto){
        AppUser appUser = new AppUser();
        appUser.setEmail(registerRequestDto.getEmail());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        appUser.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        appUser.setRole(AppRole.ROLE_USER);
        appUserRepository.save(appUser);

        String token = jwtSecurityService.generateToken(appUser);

        return RegisterResponseDto
                .builder()
                .email(registerRequestDto.getEmail())
                .jwtToken(token)
                .build();
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto){

        AppUser user = appUserRepository
                .findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Ошибка Аутентификации"));

        String token = jwtSecurityService.generateToken(user);

        return LoginResponseDto
                .builder()
                .email(loginRequestDto.getEmail())
                .jwtToken(token)
                .build();
    }

}