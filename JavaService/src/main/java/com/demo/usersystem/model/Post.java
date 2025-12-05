package com.demo.usersystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "category_snapshot", length = 50)
    private String categorySnapshot;

    @Column(name = "district_snapshot", length = 100)
    private String districtSnapshot;

    @Column(name = "contact_snapshot", length = 100)
    private String contactSnapshot;

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    public Post() {
    }

    public Post(String username, String role, String title, String detail, BigDecimal price,
                String categorySnapshot, String districtSnapshot, String contactSnapshot) {
        this.username = username;
        this.role = role;
        this.title = title;
        this.detail = detail;
        this.price = price;
        this.categorySnapshot = categorySnapshot;
        this.districtSnapshot = districtSnapshot;
        this.contactSnapshot = contactSnapshot;
        this.createTime = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategorySnapshot() {
        return categorySnapshot;
    }

    public void setCategorySnapshot(String categorySnapshot) {
        this.categorySnapshot = categorySnapshot;
    }

    public String getDistrictSnapshot() {
        return districtSnapshot;
    }

    public void setDistrictSnapshot(String districtSnapshot) {
        this.districtSnapshot = districtSnapshot;
    }

    public String getContactSnapshot() {
        return contactSnapshot;
    }

    public void setContactSnapshot(String contactSnapshot) {
        this.contactSnapshot = contactSnapshot;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}