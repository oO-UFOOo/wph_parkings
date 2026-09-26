package com.sopa.config;

import com.sopa.entity.AppUser;
import com.sopa.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatabaseInitializer {

    @Bean
    public CommandLineRunner initializeDatabase(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if users already exist
            if (userRepository.count() > 0) {
                System.out.println("✓ Database already initialized with users.");
                return;
            }

            System.out.println("Initializing database with demo users...");

            // Create UFO user
            AppUser ufoUser = new AppUser();
            ufoUser.setUsername("ufo");
            ufoUser.setPassword(passwordEncoder.encode("ufo_password_123"));
            ufoUser.setAppType("UFO");
            ufoUser.setIsActive(true);
            userRepository.save(ufoUser);
            System.out.println("✓ Created UFO user");

            // Create OpenEye user
            AppUser openeye = new AppUser();
            openeye.setUsername("openeye");
            openeye.setPassword(passwordEncoder.encode("openeye_password_123"));
            openeye.setAppType("OPENEYE");
            openeye.setIsActive(true);
            userRepository.save(openeye);
            System.out.println("✓ Created OpenEye user");

            System.out.println("✓ Database initialization complete!");
        };
    }
}
