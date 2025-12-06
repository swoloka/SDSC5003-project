package com.demo.usersystem.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "service_profiles")
public class ServiceProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Integer profileId;

    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @Column(name = "pet_type", length = 50)
    private String petType;

    @Column(name = "service_type", length = 50)
    private String serviceType;

    @Column(name = "price")
    private Double price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    public ServiceProfile() {
    }

    public ServiceProfile(String username, String petType, String serviceType, Double price, String description) {
        this.username = username;
        this.petType = petType;
        this.serviceType = serviceType;
        this.price = price;
        this.description = description;
    }

    @JsonProperty("serviceId")
    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("petType")
    public String getPetType() {
        return petType;
    }

    @JsonProperty("petType")
    public void setPetType(String petType) {
        this.petType = petType;
    }

    @JsonProperty("serviceType")
    public String getServiceType() {
        return serviceType;
    }

    @JsonProperty("serviceType")
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}