package com.sopa.controller;

import com.sopa.entity.Spot;
import com.sopa.repository.SpotRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/spots")
public class SpotController {

    private final SpotRepository spotRepository;

    public SpotController(SpotRepository spotRepository) {
        this.spotRepository = spotRepository;
    }

    @GetMapping
    public List<Spot> getAllSpots() {
        return spotRepository.findAll();
    }

    @GetMapping("/{id}")
    public Spot getSpotById(@PathVariable Long id) {
        return spotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Spot not found"));
    }

    @GetMapping("/qr/{qrCode}")
    public Spot getSpotByQrCode(@PathVariable String qrCode) {
        return spotRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Spot with QR code not found"));
    }

    @GetMapping("/client/{patrolClientId}")
    public List<Spot> getSpotsByPatrolClientId(@PathVariable Long patrolClientId) {
        return spotRepository.findByPatrolClientId(patrolClientId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Spot createSpot(@Valid @RequestBody Spot spot) {
        return spotRepository.save(spot);
    }

    @PutMapping("/{id}")
    public Spot updateSpot(@PathVariable Long id, @Valid @RequestBody Spot spotDetails) {
        Spot spot = spotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Spot not found"));

        spot.setSpotName(spotDetails.getSpotName());
        spot.setQrCode(spotDetails.getQrCode());
        spot.setLongitude(spotDetails.getLongitude());
        spot.setLatitude(spotDetails.getLatitude());
        spot.setDescription(spotDetails.getDescription());
        spot.setIsActive(spotDetails.getIsActive());

        return spotRepository.save(spot);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSpot(@PathVariable Long id) {
        if (!spotRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Spot not found");
        }
        spotRepository.deleteById(id);
    }
}
