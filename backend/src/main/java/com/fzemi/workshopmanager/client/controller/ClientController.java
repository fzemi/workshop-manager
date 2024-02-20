package com.fzemi.workshopmanager.client.controller;

import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.client.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<List<Client>> listClients() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> findClientById(@PathVariable Long id) {
        Optional<Client> foundClient = clientService.findClientById(id);
        return foundClient.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/surname/{surname}")
    public ResponseEntity<List<Client>> findClientsBySurname(@PathVariable String surname) {
        return ResponseEntity.ok(clientService.findClientsBySurname(surname));
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        Client createdClient = clientService.save(client);
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> fullUpdateClient(
            @PathVariable Long id,
            @RequestBody Client client) {
        Optional<Client> foundClient = clientService.findClientById(id);

        if (foundClient.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        client.setId(id);
        Client updatedClient = clientService.save(client);
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Client> partialUpdateClient(
            @PathVariable Long id,
            @RequestBody Client client) {
        Optional<Client> foundClient = clientService.findClientById(id);

        if (foundClient.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Client updatedClient = clientService.partialUpdate(id, client);
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }
}
