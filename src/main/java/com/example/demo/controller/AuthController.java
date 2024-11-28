package com.example.demo.controller;

import com.example.demo.model.dto.*;
import com.example.demo.service.jwt.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authorisation Controller", description = "Authorisation Controller allows to users make Authorisation and get JWT")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Registration of user in Authorisation Service")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody @Valid RegisterRequestDto registerRequestDto){
        return ResponseEntity.ok(authService.register(registerRequestDto));
    }

    @Operation(summary = "Login of user in Authorisation Service")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(  @Valid @RequestBody LoginRequestDto loginRequestDto){
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }
}