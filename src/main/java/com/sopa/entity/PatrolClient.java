package com.sopa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "patrol_clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatrolClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "client_code")
    private String clientCode;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "patrolClient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Spot> spots = new HashSet<>();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
