package com.example.demo.controller.handler;

import com.example.demo.model.error.ErrorMyAuth;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMyAuth> handleValidationExceptions(MethodArgumentNotValidException ex) {

        // Проход по всем ошибкам валидации и сбор сообщений
        List<String> stringList = ex.getBindingResult().getAllErrors()
                .stream().map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

        return new ResponseEntity<>(new ErrorMyAuth(stringList), HttpStatus.BAD_REQUEST);
    }
}
