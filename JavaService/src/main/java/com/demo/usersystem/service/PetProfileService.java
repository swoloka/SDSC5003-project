package com.demo.usersystem.service;

import com.demo.usersystem.model.PetProfile;
import com.demo.usersystem.repository.PetProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PetProfileService {

    private final PetProfileRepository petProfileRepository;

    public PetProfileService(PetProfileRepository petProfileRepository) {
        this.petProfileRepository = petProfileRepository;
    }

    public List<PetProfile> findAll() {
        return petProfileRepository.findAll();
    }

    public Optional<PetProfile> findById(Integer id) {
        return petProfileRepository.findById(id);
    }

    public List<PetProfile> findByUsername(String username) {
        return petProfileRepository.findByUsername(username);
    }

    public List<PetProfile> findByCategory(String category) {
        return petProfileRepository.findByCategory(category);
    }

    public List<PetProfile> findByKeyword(String keyword) {
        return petProfileRepository.findByKeyword(keyword);
    }

    public PetProfile save(PetProfile petProfile) {
        return petProfileRepository.save(petProfile);
    }

    public Optional<PetProfile> update(Integer id, PetProfile petProfileDetails) {
        return petProfileRepository.findById(id)
            .map(petProfile -> {
                petProfile.setCategory(petProfileDetails.getCategory());
                petProfile.setHabits(petProfileDetails.getHabits());
                return petProfileRepository.save(petProfile);
            });
    }

    public boolean deleteById(Integer id) {
        if (petProfileRepository.existsById(id)) {
            petProfileRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsById(Integer id) {
        return petProfileRepository.existsById(id);
    }
}