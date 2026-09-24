package com.sopa.controller;

import com.sopa.entity.*;
import com.sopa.repository.ParkingRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/parkings")
public class ParkingController {

    private final ParkingRepository parkingRepository;

    public ParkingController(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }

    @GetMapping
    public List<Parking> getAllParkings() {
        return parkingRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Parking addParking(@Valid @RequestBody Parking parking) {
        return parkingRepository.save(parking);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteParking(@PathVariable Long id) {
        if (!parkingRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parking not found");
        }
        parkingRepository.deleteById(id);
    }
}