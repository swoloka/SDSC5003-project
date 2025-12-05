package com.demo.usersystem.dto;

/**
 * Data Transfer Object for login requests.
 * A record is perfect here.
 */
public record LoginRequest(String username, String password) {
}

