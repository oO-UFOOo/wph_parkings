package com.sopa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "qr_spots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "spot_name", nullable = false)
    private String spotName;

    @NotBlank
    @Column(name = "qr_code", nullable = false, unique = true)
    private String qrCode;

    @NotNull
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @ManyToOne
    @JoinColumn(name = "patrol_client_id", nullable = false)
    private PatrolClient patrolClient;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
