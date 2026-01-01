package com.example.healthinsuranceweb.Service;

import com.example.healthinsuranceweb.DTO.ProfileDTO;
import com.example.healthinsuranceweb.Entity.User;
import com.example.healthinsuranceweb.Entity.UserProfile;
import com.example.healthinsuranceweb.Repository.UserProfileRepository;
import com.example.healthinsuranceweb.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Optional;

@Service
public class ProfileService {

    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;

    public ProfileService(UserRepository userRepo, UserProfileRepository profileRepo) {
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
    }

    public Optional<ProfileDTO> getByEmail(String email) {
        return profileRepo.findByUser_Email(email).map(this::toDTO);
    }

    public ProfileDTO upsertByEmail(String email, ProfileDTO dto, MultipartFile image) throws Exception {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        UserProfile profile = profileRepo.findByUser_Email(email).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setUser(user);
            return p;
        });

        // only profile fields (not login fields)
        profile.setFullName(dto.getFullName());
        profile.setNic(dto.getNic());
        profile.setDob(dto.getDob());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());

        if (image != null && !image.isEmpty()) {
            profile.setPhoto(image.getBytes());
        }

        UserProfile saved = profileRepo.save(profile);
        return toDTO(saved);
    }

    private ProfileDTO toDTO(UserProfile p) {
        ProfileDTO dto = new ProfileDTO();
        dto.setFullName(p.getFullName());
        dto.setNic(p.getNic());
        dto.setDob(p.getDob());
        dto.setPhone(p.getPhone());
        dto.setAddress(p.getAddress());
        if (p.getPhoto() != null && p.getPhoto().length > 0) {
            dto.setPhotoBase64("data:image/*;base64," +
                    Base64.getEncoder().encodeToString(p.getPhoto()));
        }
        return dto;
    }
}
