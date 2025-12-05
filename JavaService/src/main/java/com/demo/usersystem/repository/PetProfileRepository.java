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

    List<PetProfile> findByCategory(String category);

    @Query("SELECT p FROM PetProfile p WHERE p.category LIKE %:keyword% OR p.habits LIKE %:keyword%")
    List<PetProfile> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM PetProfile p WHERE p.username = :username AND p.category = :category")
    Optional<PetProfile> findByUsernameAndCategory(@Param("username") String username, @Param("category") String category);
}