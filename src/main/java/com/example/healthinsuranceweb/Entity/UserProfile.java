package com.example.healthinsuranceweb.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    private Long userId;                  // same as users.id (one-to-one PK)

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "nic", nullable = false, unique = true)
    private String nic;

    @Column(name = "dob")
    private java.time.LocalDate dob;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Lob
    @Column(name = "photo")
    private byte[] photo;                 // optional; store bytes

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Long getId() {
        return userId;
    }

    public Long getUserProfileId() {
        return userId;
    }


    // getters & setters
    // ...
}

