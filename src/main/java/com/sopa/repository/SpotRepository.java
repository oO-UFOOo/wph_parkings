package com.sopa.repository;

import com.sopa.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {
    Optional<Spot> findByQrCode(String qrCode);
    List<Spot> findByPatrolClientId(Long patrolClientId);
}
