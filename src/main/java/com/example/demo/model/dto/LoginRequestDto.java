package com.example.demo.model.dto;

import lombok.Data;
//Логин пользователя
@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
