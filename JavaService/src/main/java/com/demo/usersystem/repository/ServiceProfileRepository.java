package com.demo.usersystem.repository;

import com.demo.usersystem.model.ServiceProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceProfileRepository extends JpaRepository<ServiceProfile, Integer> {

    List<ServiceProfile> findByUsername(String username);

    List<ServiceProfile> findByCategory(String category);

    @Query("SELECT s FROM ServiceProfile s WHERE s.category LIKE %:keyword% OR s.description LIKE %:keyword%")
    List<ServiceProfile> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT s FROM ServiceProfile s WHERE s.username = :username AND s.category = :category")
    Optional<ServiceProfile> findByUsernameAndCategory(@Param("username") String username, @Param("category") String category);
}