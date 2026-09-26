package com.sopa.controller;

import com.sopa.entity.PatrolSpotTimestamp;
import com.sopa.repository.PatrolSpotTimestampRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/patrol-spot-timestamps")
public class PatrolSpotTimestampController {

    private final PatrolSpotTimestampRepository patrolSpotTimestampRepository;

    public PatrolSpotTimestampController(PatrolSpotTimestampRepository patrolSpotTimestampRepository) {
        this.patrolSpotTimestampRepository = patrolSpotTimestampRepository;
    }

    @GetMapping
    public List<PatrolSpotTimestamp> getAllPatrolSpotTimestamps() {
        return patrolSpotTimestampRepository.findAll();
    }

    @GetMapping("/{id}")
    public PatrolSpotTimestamp getPatrolSpotTimestampById(@PathVariable Long id) {
        return patrolSpotTimestampRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patrol Spot Timestamp not found"));
    }

    @GetMapping("/spot/{spotId}")
    public List<PatrolSpotTimestamp> getTimestampsBySpotId(@PathVariable Long spotId) {
        return patrolSpotTimestampRepository.findBySpotId(spotId);
    }

    @GetMapping("/client/{patrolClientId}")
    public List<PatrolSpotTimestamp> getTimestampsByPatrolClientId(@PathVariable Long patrolClientId) {
        return patrolSpotTimestampRepository.findByPatrolClientId(patrolClientId);
    }

    @GetMapping("/range")
    public List<PatrolSpotTimestamp> getTimestampsByRange(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);
        return patrolSpotTimestampRepository.findByCheckInTimestampBetween(start, end);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatrolSpotTimestamp createPatrolSpotTimestamp(@Valid @RequestBody PatrolSpotTimestamp patrolSpotTimestamp) {
        return patrolSpotTimestampRepository.save(patrolSpotTimestamp);
    }

    @PutMapping("/{id}")
    public PatrolSpotTimestamp updatePatrolSpotTimestamp(@PathVariable Long id, @Valid @RequestBody PatrolSpotTimestamp patrolSpotTimestampDetails) {
        PatrolSpotTimestamp patrolSpotTimestamp = patrolSpotTimestampRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patrol Spot Timestamp not found"));

        patrolSpotTimestamp.setCheckOutTimestamp(patrolSpotTimestampDetails.getCheckOutTimestamp());
        patrolSpotTimestamp.setNotes(patrolSpotTimestampDetails.getNotes());
        patrolSpotTimestamp.setStatus(patrolSpotTimestampDetails.getStatus());

        return patrolSpotTimestampRepository.save(patrolSpotTimestamp);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatrolSpotTimestamp(@PathVariable Long id) {
        if (!patrolSpotTimestampRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patrol Spot Timestamp not found");
        }
        patrolSpotTimestampRepository.deleteById(id);
    }
}
