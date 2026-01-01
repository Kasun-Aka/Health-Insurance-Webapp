package com.example.healthinsuranceweb.DTO;


import lombok.Data;

import java.util.Map;

@Data
public class AuthResponse {
    private boolean success;
    private String token;
    private Map<String, Object> user;

    public AuthResponse() {
    }

    public AuthResponse(boolean success, String token, Map<String, Object> user) {
        this.success = success;
        this.token = token;
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, Object> getUser() {
        return user;
    }

    public void setUser(Map<String, Object> user) {
        this.user = user;
    }
}

