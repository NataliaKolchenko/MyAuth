package com.example.demo.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterResponseDto {
    private String email;
    private String jwtToken;
    private String refreshToken;
}
