package com.demo.usersystem.controller;

import com.demo.usersystem.model.Post;
import com.demo.usersystem.service.PostService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.findAll();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<Post>> getLatestPosts() {
        List<Post> posts = postService.findLatestPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Integer id) {
        Optional<Post> post = postService.findById(id);
        return post.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Post>> getPostsByUsername(@PathVariable String username) {
        List<Post> posts = postService.findByUsername(username);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<Post>> getPostsByRole(@PathVariable String role) {
        List<Post> posts = postService.findByRole(role);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Post>> getPostsByCategory(@PathVariable String category) {
        List<Post> posts = postService.findByCategory(category);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/district/{district}")
    public ResponseEntity<List<Post>> getPostsByDistrict(@PathVariable String district) {
        List<Post> posts = postService.findByDistrict(district);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPosts(@RequestParam String keyword) {
        List<Post> posts = postService.findByKeyword(keyword);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<Post>> getPostsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        List<Post> posts = postService.findByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/date-after")
    public ResponseEntity<List<Post>> getPostsAfterDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate) {
        List<Post> posts = postService.findByCreateDateAfter(startDate);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/date-between")
    public ResponseEntity<List<Post>> getPostsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Post> posts = postService.findByCreateDateBetween(startDate, endDate);
        return ResponseEntity.ok(posts);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post savedPost = postService.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Integer id, @RequestBody Post post) {
        Optional<Post> updatedPost = postService.update(id, post);
        return updatedPost.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer id) {
        boolean deleted = postService.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getPostCount() {
        long count = postService.count();
        return ResponseEntity.ok(count);
    }
}