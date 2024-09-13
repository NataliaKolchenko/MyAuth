package com.example.demo.model.dto;

import lombok.Builder;
import lombok.Data;
//Ответ на запрос логина
@Data
@Builder
public class LoginResponseDto {
    private String email;
    private String jwtToken;
    private String refreshToken;
}
