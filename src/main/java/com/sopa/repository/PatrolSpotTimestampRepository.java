package com.sopa.repository;

import com.sopa.entity.PatrolSpotTimestamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PatrolSpotTimestampRepository extends JpaRepository<PatrolSpotTimestamp, Long> {
    List<PatrolSpotTimestamp> findBySpotId(Long spotId);
    List<PatrolSpotTimestamp> findByPatrolClientId(Long patrolClientId);
    List<PatrolSpotTimestamp> findByCheckInTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
}
