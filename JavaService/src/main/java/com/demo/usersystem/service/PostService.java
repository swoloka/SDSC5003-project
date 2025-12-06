package com.demo.usersystem.service;

import com.demo.usersystem.model.Post;
import com.demo.usersystem.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(Integer id) {
        return postRepository.findById(id);
    }

    public List<Post> findByUsername(String username) {
        return postRepository.findByUsername(username);
    }

    public List<Post> findByRole(String role) {
        return postRepository.findByRole(role);
    }

    public List<Post> findByPetType(String petType) {
        return postRepository.findByPetType(petType);
    }

    public List<Post> findByServiceType(String serviceType) {
        return postRepository.findByServiceType(serviceType);
    }

    public List<Post> findByDistrict(String district) {
        return postRepository.findByDistrict(district);
    }

    public List<Post> findByKeyword(String keyword) {
        return postRepository.findByKeyword(keyword);
    }

    public List<Post> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return postRepository.findByPriceRange(minPrice, maxPrice);
    }

    public List<Post> findByCreateDateAfter(LocalDateTime startDate) {
        return postRepository.findByCreateDateAfter(startDate);
    }

    public List<Post> findByCreateDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return postRepository.findByCreateDateBetween(startDate, endDate);
    }

    public List<Post> findLatestPosts() {
        return postRepository.findLatestPosts();
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public Optional<Post> update(Integer id, Post postDetails) {
        return postRepository.findById(id)
            .map(post -> {
                post.setRole(postDetails.getRole());
                post.setTitle(postDetails.getTitle());
                post.setPetName(postDetails.getPetName());
                post.setBreed(postDetails.getBreed());
                post.setServiceType(postDetails.getServiceType());
                post.setPrice(postDetails.getPrice());
                post.setDescription(postDetails.getDescription());
                post.setPetType(postDetails.getPetType());
                post.setDistrict(postDetails.getDistrict());
                post.setContact(postDetails.getContact());
                post.setServiceTime(postDetails.getServiceTime());
                post.setStatus(postDetails.getStatus());
                return postRepository.save(post);
            });
    }

    public boolean deleteById(Integer id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsById(Integer id) {
        return postRepository.existsById(id);
    }

    public long count() {
        return postRepository.count();
    }

    public List<Post> findByStatus(String status) {
        return postRepository.findByStatus(status);
    }

    public List<Post> findByUsernameAndStatus(String username, String status) {
        return postRepository.findByUsernameAndStatus(username, status);
    }

    public List<Post> findByRoleAndOpenStatus(String role) {
        return postRepository.findByRoleAndOpenStatus(role);
    }

    public List<Post> findByPetTypeAndOpenStatus(String petType) {
        return postRepository.findByPetTypeAndOpenStatus(petType);
    }

    public List<Post> findByRoleAndPetTypeAndOpenStatus(String role, String petType) {
        return postRepository.findByRoleAndPetTypeAndOpenStatus(role, petType);
    }

    public List<Post> findByOpenStatus() {
        return postRepository.findByOpenStatus();
    }

    public List<Post> findOpenPostsByFilters(String type, String petType, String serviceType, String district) {
        return postRepository.findOpenPostsByFilters(type, petType, serviceType, district);
    }

    public Optional<Post> updateStatus(Integer id, String status) {
        return postRepository.findById(id)
            .map(post -> {
                post.setStatus(status);
                return postRepository.save(post);
            });
    }
}