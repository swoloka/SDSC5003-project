package com.demo.usersystem.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "pet_profiles")
public class PetProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Integer profileId;

    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @Column(name = "pet_name", length = 100)
    private String petName;

    @Column(name = "pet_type", length = 50)
    private String petType;

    @Column(name = "breed", length = 50)
    private String breed;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    public PetProfile() {
    }

    public PetProfile(String username, String petName, String petType, String breed, String description) {
        this.username = username;
        this.petName = petName;
        this.petType = petType;
        this.breed = breed;
        this.description = description;
    }

    @JsonProperty("petId")
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

    @JsonProperty("petName")
    public String getPetName() {
        return petName;
    }

    @JsonProperty("petName")
    public void setPetName(String petName) {
        this.petName = petName;
    }

    @JsonProperty("petType")
    public String getPetType() {
        return petType;
    }

    @JsonProperty("petType")
    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
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