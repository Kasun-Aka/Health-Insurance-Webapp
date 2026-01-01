package com.example.healthinsuranceweb.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "insurancepackage")  // Must match your actual table name
public class InsurancePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_name")
    private String packageName;

    @Column(name = "description")
    private String description;

    // Constructors
    public InsurancePackage() {}

    public InsurancePackage(String packageName, String description) {
        this.packageName = packageName;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}