package com.example.demo.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//“Сброс” токена
@Getter
@Setter
public class RefreshTokenRequestDto {
    private String refreshToken;
}
