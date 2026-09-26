package com.sopa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "timestamps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "event_type", nullable = false)
    private String eventType; // e.g., "LOGIN", "SPOT_CHECK", "UPDATE", etc.

    @Column(name = "description")
    private String description;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "entity_type")
    private String entityType; // e.g., "SPOT", "PATROL_CLIENT", etc.

    @Column(name = "entity_id")
    private Long entityId;

    @PrePersist
    protected void onCreate() {
        eventTimestamp = LocalDateTime.now();
    }
}
