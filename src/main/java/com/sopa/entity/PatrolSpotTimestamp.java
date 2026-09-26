package com.sopa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "patrol_spot_timestamps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatrolSpotTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "spot_id", nullable = false)
    private Spot spot;

    @ManyToOne
    @JoinColumn(name = "patrol_client_id", nullable = false)
    private PatrolClient patrolClient;

    @NotNull
    @Column(name = "check_in_timestamp", nullable = false)
    private LocalDateTime checkInTimestamp;

    @Column(name = "check_out_timestamp")
    private LocalDateTime checkOutTimestamp;

    @Column(name = "notes")
    private String notes;

    @Column(name = "status")
    private String status; // e.g., "CHECKED_IN", "CHECKED_OUT", "COMPLETED"

    @PrePersist
    protected void onCreate() {
        checkInTimestamp = LocalDateTime.now();
    }
}
