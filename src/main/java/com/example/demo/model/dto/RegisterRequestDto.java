package com.example.demo.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//Регистрация пользователя
@Getter
@Setter
public class RegisterRequestDto {
    private String fullName;
    private String email;
    private String password;
}
