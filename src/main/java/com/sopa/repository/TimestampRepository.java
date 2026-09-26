package com.sopa.repository;

import com.sopa.entity.Timestamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimestampRepository extends JpaRepository<Timestamp, Long> {
    List<Timestamp> findByEventType(String eventType);
    List<Timestamp> findByUserId(Long userId);
    List<Timestamp> findByEventTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
}
