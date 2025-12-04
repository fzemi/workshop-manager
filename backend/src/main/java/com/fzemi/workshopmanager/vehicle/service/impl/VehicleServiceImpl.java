package com.fzemi.workshopmanager.vehicle.service.impl;

import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.client.repository.ClientRepository;
import com.fzemi.workshopmanager.vehicle.dto.VehicleDTO;
import com.fzemi.workshopmanager.vehicle.dto.VehicleMapper;
import com.fzemi.workshopmanager.vehicle.entity.Vehicle;
import com.fzemi.workshopmanager.vehicle.exception.VehicleNotFoundException;
import com.fzemi.workshopmanager.vehicle.repository.VehicleRepository;
import com.fzemi.workshopmanager.vehicle.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final ClientRepository clientRepository;
    private final VehicleMapper vehicleMapper;

    @Autowired
    public VehicleServiceImpl(
            VehicleRepository vehicleRepository,
            ClientRepository clientRepository,
            VehicleMapper vehicleMapper
    ) {
        this.vehicleRepository = vehicleRepository;
        this.clientRepository = clientRepository;
        this.vehicleMapper = vehicleMapper;
    }

    @Override
    public List<VehicleDTO> findAll() {
        return vehicleRepository.findAll().stream()
                .map(vehicleMapper::toVehicleDTO)
                .toList();
    }

    @Override
    public VehicleDTO findVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .map(vehicleMapper::toVehicleDTO)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle with id: " + id + " not found"));
    }

    @Override
    public VehicleDTO findVehicleByVin(String vin) {
        return vehicleRepository.findByVin(vin)
                .map(vehicleMapper::toVehicleDTO)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle with vin: " + vin + " not found"));
    }

    @Override
    public VehicleDTO findVehicleByLicencePlate(String licencePlate) {
        return vehicleRepository.findByLicencePlate(licencePlate)
                .map(vehicleMapper::toVehicleDTO)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle with licence place: " + licencePlate + " not found"));
    }

    @Override
    @Transactional
    public VehicleDTO save(Vehicle vehicle) {
        // Look up existing clients and assign them to the vehicle
        if (vehicle.getClients() != null && !vehicle.getClients().isEmpty()) {
            List<Client> managedClients = vehicle.getClients().stream()
                    .filter(client -> client.getId() != null)
                    .map(client -> clientRepository.findById(client.getId())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Client with id: " + client.getId() + " not found")))
                    .toList();
            vehicle.setClients(new ArrayList<>(managedClients));

            // Update the vehicle list in each client
            managedClients.forEach(client -> {
                if (!client.getVehicles().contains(vehicle)) {
                    client.getVehicles().add(vehicle);
                }
            });
        }

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toVehicleDTO(savedVehicle);
    }

    @Override
    @Transactional
    public VehicleDTO fullUpdate(Vehicle vehicle) {
        Optional<Vehicle> existingVehicle = vehicleRepository.findById(vehicle.getId());

        if (existingVehicle.isEmpty()) {
            throw new VehicleNotFoundException("Cannot update vehicle with id: " + vehicle.getId());
        }

        vehicle.setClients(existingVehicle.get().getClients());
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toVehicleDTO(updatedVehicle);
    }

    @Override
    @Transactional
    public VehicleDTO partialUpdate(Long id, Vehicle vehicle) {
        return vehicleRepository.findById(id).map(existingVehicle -> {
                    Optional.ofNullable(vehicle.getVin()).ifPresent(existingVehicle::setVin);
                    Optional.ofNullable(vehicle.getManufacturer()).ifPresent(existingVehicle::setManufacturer);
                    Optional.ofNullable(vehicle.getModel()).ifPresent(existingVehicle::setModel);
                    Optional.ofNullable(vehicle.getLicencePlate()).ifPresent(existingVehicle::setLicencePlate);
                    Optional.ofNullable(vehicle.getProductionDate()).ifPresent(existingVehicle::setProductionDate);
                    Optional.ofNullable(vehicle.getColor()).ifPresent(existingVehicle::setColor);
                    Optional.ofNullable(vehicle.getEngineCapacity()).ifPresent(existingVehicle::setEngineCapacity);
                    Optional.ofNullable(vehicle.getFuelType()).ifPresent(existingVehicle::setFuelType);
                    Optional.ofNullable(vehicle.getPower()).ifPresent(existingVehicle::setPower);

                    // Replace clients - handle additions and removals
                    if (vehicle.getClients() != null) {
                        // Get IDs of clients to keep
                        List<Long> newClientIds = vehicle.getClients().stream()
                                .map(Client::getId)
                                .filter(clientId -> clientId != null)
                                .toList();

                        // Remove vehicle from clients that are no longer assigned
                        List<Client> clientsToRemove = existingVehicle.getClients().stream()
                                .filter(c -> !newClientIds.contains(c.getId()))
                                .toList();
                        clientsToRemove.forEach(client -> {
                            client.getVehicles().remove(existingVehicle);
                            existingVehicle.getClients().remove(client);
                        });

                        // Add new clients
                        newClientIds.forEach(clientId -> {
                            boolean alreadyExists = existingVehicle.getClients().stream()
                                    .anyMatch(c -> c.getId().equals(clientId));
                            if (!alreadyExists) {
                                clientRepository.findById(clientId).ifPresent(client -> {
                                    existingVehicle.getClients().add(client);
                                    client.getVehicles().add(existingVehicle);
                                });
                            }
                        });
                    }

                    return vehicleRepository.save(existingVehicle);
                })
                .map(vehicleMapper::toVehicleDTO)
                .orElseThrow(() -> new VehicleNotFoundException("Cannot update vehicle with id: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new VehicleNotFoundException("Vehicle with id: " + id + " not found");
        }

        vehicleRepository.deleteById(id);
    }
}
