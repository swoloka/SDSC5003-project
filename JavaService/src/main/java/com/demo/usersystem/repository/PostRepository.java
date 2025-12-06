package com.demo.usersystem.repository;

import com.demo.usersystem.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findByUsername(String username);

    List<Post> findByRole(String role);

    List<Post> findByPetType(String petType);

    List<Post> findByServiceType(String serviceType);

    List<Post> findByDistrict(String district);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.petName LIKE %:keyword% OR p.breed LIKE %:keyword% OR p.serviceType LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Post> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Post> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT p FROM Post p WHERE p.createTime >= :startDate")
    List<Post> findByCreateDateAfter(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT p FROM Post p WHERE p.createTime BETWEEN :startDate AND :endDate")
    List<Post> findByCreateDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Post p ORDER BY p.createTime DESC")
    List<Post> findLatestPosts();

    @Query("SELECT p FROM Post p WHERE p.username = :username AND p.title = :title")
    List<Post> findByUsernameAndTitle(@Param("username") String username, @Param("title") String title);
}