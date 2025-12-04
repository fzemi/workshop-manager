package com.fzemi.workshopmanager.client.service.impl;

import com.fzemi.workshopmanager.client.dto.ClientDTO;
import com.fzemi.workshopmanager.client.dto.ClientMapper;
import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.client.exception.ClientNotFoundException;
import com.fzemi.workshopmanager.client.repository.ClientRepository;
import com.fzemi.workshopmanager.client.service.ClientService;
import com.fzemi.workshopmanager.vehicle.entity.Vehicle;
import com.fzemi.workshopmanager.vehicle.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;
    private final ClientMapper clientMapper;

    @Autowired
    public ClientServiceImpl(
            ClientRepository clientRepository,
            VehicleRepository vehicleRepository,
            ClientMapper clientMapper
    ) {
        this.clientRepository = clientRepository;
        this.vehicleRepository = vehicleRepository;
        this.clientMapper = clientMapper;
    }

    @Override
    public List<ClientDTO> findAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toClientDTO)
                .toList();
    }

    @Override
    public ClientDTO findClientById(Long id) {
        return clientRepository.findById(id)
                .map(clientMapper::toClientDTO)
                .orElseThrow(() -> new ClientNotFoundException("Client with id: " + id + " not found"));
    }

    @Override
    public List<ClientDTO> findClientsBySurname(String surname) {
        return clientRepository.findBySurname(surname).stream()
                .map(clientMapper::toClientDTO)
                .toList();
    }

    @Override
    @Transactional
    public ClientDTO save(Client client) {
        // Look up existing vehicles and assign them to the client
        if (client.getVehicles() != null && !client.getVehicles().isEmpty()) {
            List<Vehicle> managedVehicles = client.getVehicles().stream()
                    .filter(vehicle -> vehicle.getId() != null)
                    .map(vehicle -> vehicleRepository.findById(vehicle.getId())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Vehicle with id: " + vehicle.getId() + " not found")))
                    .toList();
            client.setVehicles(new java.util.ArrayList<>(managedVehicles));

            // Update the client list in each vehicle
            managedVehicles.forEach(vehicle -> {
                if (!vehicle.getClients().contains(client)) {
                    vehicle.getClients().add(client);
                }
            });
        }

        Client savedClient = clientRepository.save(client);
        return clientMapper.toClientDTO(savedClient);
    }

    @Override
    @Transactional
    public ClientDTO fullUpdate(Client client) {
        Optional<Client> existingClient = clientRepository.findById(client.getId());

        if (existingClient.isEmpty()) {
            throw new ClientNotFoundException("Cannot update client with id: " + client.getId());
        }

        client.setVehicles(existingClient.get().getVehicles());

        Client updatedClient = clientRepository.save(client);
        return clientMapper.toClientDTO(updatedClient);
    }


    @Override
    @Transactional
    public ClientDTO partialUpdate(Long id, Client client) {
        return clientRepository.findById(id).map(existingClient -> {
                    Optional.ofNullable(client.getFirstname()).ifPresent(existingClient::setFirstname);
                    Optional.ofNullable(client.getSurname()).ifPresent(existingClient::setSurname);
                    Optional.ofNullable(client.getPesel()).ifPresent(existingClient::setPesel);
                    Optional.ofNullable(client.getNip()).ifPresent(existingClient::setNip);
                    Optional.ofNullable(client.getPhoneNumber()).ifPresent(existingClient::setPhoneNumber);
                    Optional.ofNullable(client.getEmail()).ifPresent(existingClient::setEmail);
                    Optional.ofNullable(client.getCountry()).ifPresent(existingClient::setCountry);
                    Optional.ofNullable(client.getPostalCode()).ifPresent(existingClient::setPostalCode);
                    Optional.ofNullable(client.getCity()).ifPresent(existingClient::setCity);
                    Optional.ofNullable(client.getAddress()).ifPresent(existingClient::setAddress);
                    Optional.ofNullable(client.getBirthDate()).ifPresent(existingClient::setBirthDate);

                    // Replace vehicles - handle additions and removals
                    if (client.getVehicles() != null) {
                        // Get IDs of vehicles to keep
                        List<Long> newVehicleIds = client.getVehicles().stream()
                                .map(Vehicle::getId)
                                .filter(vehicleId -> vehicleId != null)
                                .toList();

                        // Remove client from vehicles that are no longer assigned
                        List<Vehicle> vehiclesToRemove = existingClient.getVehicles().stream()
                                .filter(v -> !newVehicleIds.contains(v.getId()))
                                .toList();
                        vehiclesToRemove.forEach(vehicle -> {
                            vehicle.getClients().remove(existingClient);
                            existingClient.getVehicles().remove(vehicle);
                        });

                        // Add new vehicles
                        newVehicleIds.forEach(vehicleId -> {
                            boolean alreadyExists = existingClient.getVehicles().stream()
                                    .anyMatch(v -> v.getId().equals(vehicleId));
                            if (!alreadyExists) {
                                vehicleRepository.findById(vehicleId).ifPresent(vehicle -> {
                                    existingClient.getVehicles().add(vehicle);
                                    vehicle.getClients().add(existingClient);
                                });
                            }
                        });
                    }

                    return clientRepository.save(existingClient);
                })
                .map(clientMapper::toClientDTO)
                .orElseThrow(() -> new ClientNotFoundException("Cannot update client with id: " + id));
    }
}
