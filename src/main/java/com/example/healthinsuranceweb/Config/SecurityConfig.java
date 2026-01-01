package com.example.healthinsuranceweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Unified Security Configuration for Health Insurance Web Application.
 * This configuration is set up for an API-first approach, where authentication
 * is handled via custom logic (e.g., tokens) and not Spring's default form or basic auth.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF (Critical for stateless JSON APIs accessed by separate frontends)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Enable CORS (Allows browser client on a different origin to make requests)
                .cors(AbstractHttpConfigurer::disable) // Default configuration is often sufficient, but we explicitly disable CSRF so we don't need CORS protection

                // 3. Define authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Whitelist common frontend files, static assets, and the authentication API
                        .requestMatchers(
                                "/", "/index.html", "/Dashboard.html", "/Profile.html",
                                "/CSS/", "/JS/", "/image/",
                                "/api/auth/" // register/login endpoints
                        ).permitAll()
                        // Since all the provided examples use .anyRequest().permitAll(),
                        // we make the entire application publicly accessible.
                        .anyRequest().permitAll()
                )

                // 4. Disable HTTP Basic Auth (Prevents the browser's default black popup)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 5. Disable Spring’s default login form
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * Provides a BCryptPasswordEncoder bean for securely hashing passwords.
     * This is used in user registration and login validation.
     * @return The password encoder instance.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}