package com.demo.usersystem.service;

import com.demo.usersystem.model.User;
import com.demo.usersystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<String> login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            String token = UUID.randomUUID().toString();
            return Optional.of(token);
        }

        return Optional.empty();
    }
}
