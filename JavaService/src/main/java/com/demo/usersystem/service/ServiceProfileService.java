package com.demo.usersystem.service;

import com.demo.usersystem.model.ServiceProfile;
import com.demo.usersystem.repository.ServiceProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceProfileService {

    private final ServiceProfileRepository serviceProfileRepository;

    public ServiceProfileService(ServiceProfileRepository serviceProfileRepository) {
        this.serviceProfileRepository = serviceProfileRepository;
    }

    public List<ServiceProfile> findAll() {
        return serviceProfileRepository.findAll();
    }

    public Optional<ServiceProfile> findById(Integer id) {
        return serviceProfileRepository.findById(id);
    }

    public List<ServiceProfile> findByUsername(String username) {
        return serviceProfileRepository.findByUsername(username);
    }

    public List<ServiceProfile> findByCategory(String category) {
        return serviceProfileRepository.findByCategory(category);
    }

    public List<ServiceProfile> findByKeyword(String keyword) {
        return serviceProfileRepository.findByKeyword(keyword);
    }

    public ServiceProfile save(ServiceProfile serviceProfile) {
        return serviceProfileRepository.save(serviceProfile);
    }

    public Optional<ServiceProfile> update(Integer id, ServiceProfile serviceProfileDetails) {
        return serviceProfileRepository.findById(id)
            .map(serviceProfile -> {
                serviceProfile.setCategory(serviceProfileDetails.getCategory());
                serviceProfile.setDescription(serviceProfileDetails.getDescription());
                return serviceProfileRepository.save(serviceProfile);
            });
    }

    public boolean deleteById(Integer id) {
        if (serviceProfileRepository.existsById(id)) {
            serviceProfileRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsById(Integer id) {
        return serviceProfileRepository.existsById(id);
    }
}