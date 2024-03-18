package com.fzemi.workshopmanager.vehicle.service.impl;

import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.client.repository.ClientRepository;
import com.fzemi.workshopmanager.vehicle.dto.VehicleDTO;
import com.fzemi.workshopmanager.vehicle.dto.VehicleMapper;
import com.fzemi.workshopmanager.vehicle.entity.Vehicle;
import com.fzemi.workshopmanager.vehicle.repository.VehicleRepository;
import com.fzemi.workshopmanager.vehicle.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final ClientRepository clientRepository;
    private final VehicleMapper vehicleMapper;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              ClientRepository clientRepository,
                              VehicleMapper vehicleMapper) {
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
    public Optional<VehicleDTO> findVehicleById(Long id) {
        return vehicleRepository.findById(id).map(vehicleMapper::toVehicleDTO);
    }

    @Override
    public Optional<VehicleDTO> findVehicleByVin(String vin) {
        return vehicleRepository.findByVin(vin).map(vehicleMapper::toVehicleDTO);
    }

    @Override
    public Optional<VehicleDTO> findVehicleByLicencePlate(String licencePlate) {
        return vehicleRepository.findByLicencePlate(licencePlate).map(vehicleMapper::toVehicleDTO);
    }

    @Override
    public VehicleDTO save(Vehicle vehicle) {
        // Means that we are updating the vehicle not creating a new one
        if (vehicle.getId() != null) {
            Optional<Vehicle> existingVehicle = vehicleRepository.findById(vehicle.getId());
            existingVehicle.ifPresent(value -> vehicle.setClients(value.getClients()));
        }

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return vehicleMapper.toVehicleDTO(savedVehicle);
    }

    @Override
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

                    // add new clients to the vehicle
                    if (vehicle.getClients() != null && !vehicle.getClients().isEmpty()) {
                        List<Long> existingClientsIds = existingVehicle.getClients().stream()
                                .map(Client::getId)
                                .toList();

                        vehicle.getClients().forEach(client -> {
                            if (!existingClientsIds.contains(client.getId())) {
                                Optional<Client> foundClient = clientRepository.findById(client.getId());
                                foundClient.ifPresent(existingVehicle.getClients()::add);

                                // update the vehicle list in the client
                                foundClient.ifPresent(value -> value.getVehicles().add(existingVehicle));
                            }
                        });
                    }

                    return vehicleRepository.save(existingVehicle);
                })
                .map(vehicleMapper::toVehicleDTO)
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        vehicleRepository.findById(id).ifPresent(vehicleRepository::delete);
    }
}
