package com.fzemi.workshopmanager.client.service.impl;

import com.fzemi.workshopmanager.client.dto.ClientDTO;
import com.fzemi.workshopmanager.client.dto.ClientMapper;
import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.client.repository.ClientRepository;
import com.fzemi.workshopmanager.client.service.ClientService;
import com.fzemi.workshopmanager.vehicle.entity.Vehicle;
import com.fzemi.workshopmanager.vehicle.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;
    private final ClientMapper clientMapper;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository,
                             VehicleRepository vehicleRepository,
                             ClientMapper clientMapper) {
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
    public Optional<ClientDTO> findClientById(Long id) {
        return clientRepository.findById(id).map(clientMapper::toClientDTO);
    }

    @Override
    public List<ClientDTO> findClientsBySurname(String surname) {
        return clientRepository.findBySurname(surname).stream()
                .map(clientMapper::toClientDTO)
                .toList();
    }

    @Override
    public ClientDTO save(Client client) {
        // Means that we are updating the client not creating a new one
        if (client.getId() != null) {
            Optional<Client> existingClient = clientRepository.findById(client.getId());
            existingClient.ifPresent(value -> client.setVehicles(value.getVehicles()));
        }

        Client savedClient = clientRepository.save(client);

        return clientMapper.toClientDTO(savedClient);
    }

    @Override
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

                    // add new vehicles to the client
                    if (client.getVehicles() != null && !client.getVehicles().isEmpty()) {
                        List<Long> existingClientVehicleIds = existingClient.getVehicles().stream()
                                .map(Vehicle::getId)
                                .toList();

                        client.getVehicles().forEach(vehicle -> {
                            if (!existingClientVehicleIds.contains(vehicle.getId())) {
                                Optional<Vehicle> foundVehicle = vehicleRepository.findById(vehicle.getId());
                                foundVehicle.ifPresent(existingClient.getVehicles()::add);

                                // update the client list in the vehicle
                                foundVehicle.ifPresent(value -> value.getClients().add(existingClient));
                            }
                        });
                    }

                    return clientRepository.save(existingClient);
                })
                .map(clientMapper::toClientDTO)
                .orElse(null);
    }
}
