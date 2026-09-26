package com.sopa.repository;

import com.sopa.entity.PatrolClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatrolClientRepository extends JpaRepository<PatrolClient, Long> {
}
