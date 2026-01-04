package com.example.healthinsuranceweb.DTO;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long userId;
    private String packageName;
    private String comment;
    private int rating;
}