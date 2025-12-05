package com.demo.usersystem.dto;

/**
 * LoginResponse
 *
 * @author gaowz47620
 * @date
 **/
public record LoginResponse(boolean success, String message, String token) {
    public LoginResponse(boolean success, String message) {
        this(success, message, null);
    }
}