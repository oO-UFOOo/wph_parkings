package com.sopa.controller;

import com.sopa.entity.PatrolClient;
import com.sopa.repository.PatrolClientRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/patrol-clients")
public class PatrolClientController {

    private final PatrolClientRepository patrolClientRepository;

    public PatrolClientController(PatrolClientRepository patrolClientRepository) {
        this.patrolClientRepository = patrolClientRepository;
    }

    @GetMapping
    public List<PatrolClient> getAllPatrolClients() {
        return patrolClientRepository.findAll();
    }

    @GetMapping("/{id}")
    public PatrolClient getPatrolClientById(@PathVariable Long id) {
        return patrolClientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patrol Client not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatrolClient createPatrolClient(@Valid @RequestBody PatrolClient patrolClient) {
        return patrolClientRepository.save(patrolClient);
    }

    @PutMapping("/{id}")
    public PatrolClient updatePatrolClient(@PathVariable Long id, @Valid @RequestBody PatrolClient patrolClientDetails) {
        PatrolClient patrolClient = patrolClientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patrol Client not found"));

        patrolClient.setClientName(patrolClientDetails.getClientName());
        patrolClient.setClientCode(patrolClientDetails.getClientCode());
        patrolClient.setDescription(patrolClientDetails.getDescription());
        patrolClient.setIsActive(patrolClientDetails.getIsActive());

        return patrolClientRepository.save(patrolClient);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatrolClient(@PathVariable Long id) {
        if (!patrolClientRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patrol Client not found");
        }
        patrolClientRepository.deleteById(id);
    }
}
