package com.sopa.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails ufoUser = User.builder()
                .username("ufo")
                .password(passwordEncoder().encode("ufo_password_123"))
                .roles("UFO_USER")
                .build();

        UserDetails openEyeUser = User.builder()
                .username("openeye")
                .password(passwordEncoder().encode("openeye_password_123"))
                .roles("OPENEYE_USER")
                .build();

        return new InMemoryUserDetailsManager(ufoUser, openEyeUser);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/parkings/**").authenticated()
                .requestMatchers("/api/patrol-clients/**").authenticated()
                .requestMatchers("/api/spots/**").authenticated()
                .requestMatchers("/api/timestamps/**").authenticated()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
        return http.build();
    }
}
