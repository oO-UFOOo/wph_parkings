package com.sopa.controller;

import com.sopa.entity.AppUser;
import com.sopa.repository.AppUserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class AppUserController {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserController(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<AppUser> getAllUsers() {
        return appUserRepository.findAll();
    }

    @GetMapping("/{id}")
    public AppUser getUserById(@PathVariable Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @GetMapping("/username/{username}")
    public AppUser getUserByUsername(@PathVariable String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppUser createUser(@Valid @RequestBody AppUser appUser) {
        // Encode password before saving
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return appUserRepository.save(appUser);
    }

    @PutMapping("/{id}")
    public AppUser updateUser(@PathVariable Long id, @Valid @RequestBody AppUser appUserDetails) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        appUser.setUsername(appUserDetails.getUsername());
        if (appUserDetails.getPassword() != null && !appUserDetails.getPassword().isEmpty()) {
            appUser.setPassword(passwordEncoder.encode(appUserDetails.getPassword()));
        }
        appUser.setAppType(appUserDetails.getAppType());
        appUser.setIsActive(appUserDetails.getIsActive());

        return appUserRepository.save(appUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        if (!appUserRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        appUserRepository.deleteById(id);
    }
}
