package com.demo.usersystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @Column(name = "pet_name", length = 50)
    @JsonProperty("petName")
    private String petName;

    @Column(name = "breed", length = 50)
    private String breed;

    @Column(name = "service_type", length = 50)
    @JsonProperty("serviceType")
    private String serviceType;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "pet_type", length = 50)
    private String petType;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "contact", length = 100)
    private String contact;

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    public Post() {
    }

    public Post(String username, String role, String title, String petName, String breed,
                String serviceType, BigDecimal price, String description, String petType,
                String district, String contact) {
        this.username = username;
        this.role = role;
        this.title = title;
        this.petName = petName;
        this.breed = breed;
        this.serviceType = serviceType;
        this.price = price;
        this.description = description;
        this.petType = petType;
        this.district = district;
        this.contact = contact;
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

    @JsonProperty("petName")
    public String getPetName() {
        return petName;
    }

    @JsonProperty("petName")
    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    @JsonProperty("serviceType")
    public String getServiceType() {
        return serviceType;
    }

    @JsonProperty("serviceType")
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("petType")
    public String getPetType() {
        return petType;
    }

    @JsonProperty("petType")
    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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