package com.demo.usersystem.repository;

import com.demo.usersystem.model.PetProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetProfileRepository extends JpaRepository<PetProfile, Integer> {

    List<PetProfile> findByUsername(String username);

    List<PetProfile> findByPetType(String petType);

    @Query("SELECT p FROM PetProfile p WHERE p.petType LIKE %:keyword% OR p.petName LIKE %:keyword% OR p.breed LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<PetProfile> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM PetProfile p WHERE p.username = :username AND p.petType = :petType")
    Optional<PetProfile> findByUsernameAndPetType(@Param("username") String username, @Param("petType") String petType);
}