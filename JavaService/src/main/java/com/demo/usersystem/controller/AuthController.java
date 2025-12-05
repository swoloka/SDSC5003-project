package com.demo.usersystem.controller;

import com.demo.usersystem.dto.LoginRequest;
import com.demo.usersystem.dto.LoginResponse;
import com.demo.usersystem.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Optional<String> tokenOptional = authService.login(loginRequest.username(), loginRequest.password());

        if (tokenOptional.isPresent()) {
            return ResponseEntity.ok(new LoginResponse(true, "登录成功", tokenOptional.get()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse(false, "用户名或密码无效", null));
        }
    }
}
