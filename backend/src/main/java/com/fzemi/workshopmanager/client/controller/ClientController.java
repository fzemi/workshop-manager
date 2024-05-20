package com.fzemi.workshopmanager.client.controller;

import com.fzemi.workshopmanager.client.dto.ClientDTO;
import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.client.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<List<ClientDTO>> listClients() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> findClientById(@PathVariable Long id) {
        ClientDTO foundClientDTO = clientService.findClientById(id);
        return ResponseEntity.ok(foundClientDTO);
    }

    @GetMapping("/surname/{surname}")
    public ResponseEntity<List<ClientDTO>> findClientsBySurname(@PathVariable String surname) {
        return ResponseEntity.ok(clientService.findClientsBySurname(surname));
    }

    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@RequestBody Client client) {
        ClientDTO createdClientDTO = clientService.save(client);
        return new ResponseEntity<>(createdClientDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> fullUpdateClient(
            @PathVariable Long id,
            @RequestBody Client client
    ) {
        client.setId(id);
        ClientDTO updatedClientDTO = clientService.fullUpdate(client);
        return new ResponseEntity<>(updatedClientDTO, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClientDTO> partialUpdateClient(
            @PathVariable Long id,
            @RequestBody Client client
    ) {
        ClientDTO updatedClientDTO = clientService.partialUpdate(id, client);
        return new ResponseEntity<>(updatedClientDTO, HttpStatus.OK);
    }
}
