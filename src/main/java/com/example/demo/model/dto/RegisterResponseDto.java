package com.example.demo.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponseDto {
    private String email;
    private String fullName;
    private String jwtToken;
    private String refreshToken;
}
