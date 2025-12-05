package com.demo.usersystem.repository;

import com.demo.usersystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    List<User> findByDistrict(String district);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.district LIKE %:keyword% OR u.contact LIKE %:keyword%")
    List<User> findByKeyword(@Param("keyword") String keyword);
}
