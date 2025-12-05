package com.demo.usersystem.service;

import com.demo.usersystem.model.User;
import com.demo.usersystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findByDistrict(String district) {
        return userRepository.findByDistrict(district);
    }

    public List<User> findByKeyword(String keyword) {
        return userRepository.findByKeyword(keyword);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> update(String username, User userDetails) {
        return userRepository.findByUsername(username)
            .map(user -> {
                user.setPassword(userDetails.getPassword());
                user.setDistrict(userDetails.getDistrict());
                user.setContact(userDetails.getContact());
                return userRepository.save(user);
            });
    }

    public boolean deleteByUsername(String username) {
        if (userRepository.existsById(username)) {
            userRepository.deleteById(username);
            return true;
        }
        return false;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsById(username);
    }

    public long count() {
        return userRepository.count();
    }
}