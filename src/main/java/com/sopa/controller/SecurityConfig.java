package com.sopa.controller;

import com.sopa.repository.AppUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(AppUserRepository appUserRepository) {
        // Load users from the database instead of hardcoded in-memory users
        return username -> appUserRepository.findByUsername(username)
                .map(appUser -> org.springframework.security.core.userdetails.User.builder()
                        .username(appUser.getUsername())
                        .password(appUser.getPassword())
                        .roles(appUser.getAppType())
                        .accountLocked(!appUser.getIsActive())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/dashboard.html", "/favicon.ico", "/sopa.png", "/img.png", "/img_1.png", "/2.png", "/error").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/parkings/**").authenticated()
                        .requestMatchers("/api/patrol-clients/**").authenticated()
                        .requestMatchers("/api/spots/**").authenticated()
                        .requestMatchers("/api/timestamps/**").authenticated()
                        .requestMatchers("/api/patrol-spot-timestamps/**").authenticated()
                        .requestMatchers("/api/users/**").authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(basic -> basic.realmName("SOPA"))
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());
        return http.build();
    }
}
