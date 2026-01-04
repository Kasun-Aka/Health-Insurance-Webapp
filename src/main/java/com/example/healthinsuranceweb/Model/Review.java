package com.example.healthinsuranceweb.Model;

import com.example.healthinsuranceweb.Entity.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "review")
public class Review {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"payment", "password", "createdAt"}) // Don't leak passwords!
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_package_id", nullable = false)
    @JsonIgnoreProperties("payment")
    private policy insurancePackage;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "comment")
    private String comment;

    // Constructors
    public Review() {}

}