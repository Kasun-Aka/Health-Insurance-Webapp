package com.example.healthinsuranceweb.Controller;

import com.example.healthinsuranceweb.DTO.ProfileDTO;
import com.example.healthinsuranceweb.Service.ProfileService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public Map<String, Object> me(@RequestParam String email) {
        Map<String, Object> resp = new HashMap<String, Object>();
        resp.put("success", true);
        resp.put("data", service.getByEmail(email).orElse(null));
        return resp;
    }

    // multipart form: text fields + optional image
    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> update(
            @RequestParam String email,
            @RequestPart("fullName") String fullName,
            @RequestPart("nic") String nic,
            @RequestPart(value = "dob", required = false) String dob,
            @RequestPart(value = "phone", required = false) String phone,
            @RequestPart(value = "address", required = false) String address,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {

        ProfileDTO dto = new ProfileDTO();
        dto.setFullName(fullName);
        dto.setNic(nic);
        if (dob != null && !dob.trim().isEmpty()) {
            dto.setDob(java.time.LocalDate.parse(dob)); // expects yyyy-MM-dd
        }
        dto.setPhone(phone);
        dto.setAddress(address);

        ProfileDTO out = service.upsertByEmail(email, dto, image);

        Map<String, Object> resp = new HashMap<String, Object>();
        resp.put("success", true);
        resp.put("data", out);
        return resp;
    }
}
