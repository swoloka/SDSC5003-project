package com.demo.usersystem.controller;

import com.demo.usersystem.model.ServiceProfile;
import com.demo.usersystem.service.ServiceProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/service-profiles")
public class ServiceProfileController {

    private final ServiceProfileService serviceProfileService;

    public ServiceProfileController(ServiceProfileService serviceProfileService) {
        this.serviceProfileService = serviceProfileService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceProfile>> getAllServiceProfiles() {
        List<ServiceProfile> serviceProfiles = serviceProfileService.findAll();
        return ResponseEntity.ok(serviceProfiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceProfile> getServiceProfileById(@PathVariable Integer id) {
        Optional<ServiceProfile> serviceProfile = serviceProfileService.findById(id);
        return serviceProfile.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<ServiceProfile>> getServiceProfilesByUsername(@PathVariable String username) {
        List<ServiceProfile> serviceProfiles = serviceProfileService.findByUsername(username);
        return ResponseEntity.ok(serviceProfiles);
    }

    @GetMapping("/pet-type/{petType}")
    public ResponseEntity<List<ServiceProfile>> getServiceProfilesByPetType(@PathVariable String petType) {
        List<ServiceProfile> serviceProfiles = serviceProfileService.findByPetType(petType);
        return ResponseEntity.ok(serviceProfiles);
    }

    @GetMapping("/service-type/{serviceType}")
    public ResponseEntity<List<ServiceProfile>> getServiceProfilesByServiceType(@PathVariable String serviceType) {
        List<ServiceProfile> serviceProfiles = serviceProfileService.findByServiceType(serviceType);
        return ResponseEntity.ok(serviceProfiles);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ServiceProfile>> searchServiceProfiles(@RequestParam String keyword) {
        List<ServiceProfile> serviceProfiles = serviceProfileService.findByKeyword(keyword);
        return ResponseEntity.ok(serviceProfiles);
    }

    @PostMapping
    public ResponseEntity<ServiceProfile> createServiceProfile(@RequestBody ServiceProfile serviceProfile) {
        ServiceProfile savedServiceProfile = serviceProfileService.save(serviceProfile);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedServiceProfile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceProfile> updateServiceProfile(@PathVariable Integer id, @RequestBody ServiceProfile serviceProfile) {
        Optional<ServiceProfile> updatedServiceProfile = serviceProfileService.update(id, serviceProfile);
        return updatedServiceProfile.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceProfile(@PathVariable Integer id) {
        boolean deleted = serviceProfileService.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}