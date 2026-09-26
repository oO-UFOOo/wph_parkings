package com.sopa.controller;

import com.sopa.entity.Timestamp;
import com.sopa.repository.TimestampRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/timestamps")
public class TimestampController {

    private final TimestampRepository timestampRepository;

    public TimestampController(TimestampRepository timestampRepository) {
        this.timestampRepository = timestampRepository;
    }

    @GetMapping
    public List<Timestamp> getAllTimestamps() {
        return timestampRepository.findAll();
    }

    @GetMapping("/{id}")
    public Timestamp getTimestampById(@PathVariable Long id) {
        return timestampRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Timestamp not found"));
    }

    @GetMapping("/event/{eventType}")
    public List<Timestamp> getTimestampsByEventType(@PathVariable String eventType) {
        return timestampRepository.findByEventType(eventType);
    }

    @GetMapping("/user/{userId}")
    public List<Timestamp> getTimestampsByUserId(@PathVariable Long userId) {
        return timestampRepository.findByUserId(userId);
    }

    @GetMapping("/range")
    public List<Timestamp> getTimestampsByRange(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);
        return timestampRepository.findByEventTimestampBetween(start, end);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Timestamp createTimestamp(@Valid @RequestBody Timestamp timestamp) {
        return timestampRepository.save(timestamp);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTimestamp(@PathVariable Long id) {
        if (!timestampRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Timestamp not found");
        }
        timestampRepository.deleteById(id);
    }
}
