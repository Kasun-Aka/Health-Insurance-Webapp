package com.example.healthinsuranceweb.Controller;



import com.example.healthinsuranceweb.DTO.AuthResponse;
import com.example.healthinsuranceweb.DTO.LoginRequest;
import com.example.healthinsuranceweb.DTO.RegisterRequest;
import com.example.healthinsuranceweb.Service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // adjust if needed
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterRequest req) {
        // returns Map<String,Object> (success/message)
        return service.register(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        // ALWAYS returns AuthResponse (never Map)
        return service.login(req);
    }


}

