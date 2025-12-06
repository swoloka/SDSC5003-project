package com.demo.usersystem.controller;

import com.demo.usersystem.model.PetProfile;
import com.demo.usersystem.service.PetProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pet-profiles")
public class PetProfileController {

    private final PetProfileService petProfileService;

    public PetProfileController(PetProfileService petProfileService) {
        this.petProfileService = petProfileService;
    }

    @GetMapping
    public ResponseEntity<List<PetProfile>> getAllPetProfiles() {
        List<PetProfile> petProfiles = petProfileService.findAll();
        return ResponseEntity.ok(petProfiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetProfile> getPetProfileById(@PathVariable Integer id) {
        Optional<PetProfile> petProfile = petProfileService.findById(id);
        return petProfile.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<PetProfile>> getPetProfilesByUsername(@PathVariable String username) {
        List<PetProfile> petProfiles = petProfileService.findByUsername(username);
        return ResponseEntity.ok(petProfiles);
    }

    @GetMapping("/type/{petType}")
    public ResponseEntity<List<PetProfile>> getPetProfilesByPetType(@PathVariable String petType) {
        List<PetProfile> petProfiles = petProfileService.findByPetType(petType);
        return ResponseEntity.ok(petProfiles);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PetProfile>> searchPetProfiles(@RequestParam String keyword) {
        List<PetProfile> petProfiles = petProfileService.findByKeyword(keyword);
        return ResponseEntity.ok(petProfiles);
    }

    @PostMapping
    public ResponseEntity<PetProfile> createPetProfile(@RequestBody PetProfile petProfile) {
        PetProfile savedPetProfile = petProfileService.save(petProfile);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPetProfile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetProfile> updatePetProfile(@PathVariable Integer id, @RequestBody PetProfile petProfile) {
        Optional<PetProfile> updatedPetProfile = petProfileService.update(id, petProfile);
        return updatedPetProfile.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePetProfile(@PathVariable Integer id) {
        boolean deleted = petProfileService.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}