package com.fzemi.workshopmanager.vehicle.service.impl;

import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.client.repository.ClientRepository;
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

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              ClientRepository clientRepository) {
        this.vehicleRepository = vehicleRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    @Override
    public Optional<Vehicle> findVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    @Override
    public Optional<Vehicle> findVehicleByVin(String vin) {
        return vehicleRepository.findByVin(vin);
    }

    @Override
    public Optional<Vehicle> findVehicleByLicencePlate(String licencePlate) {
        return vehicleRepository.findByLicencePlate(licencePlate);
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle partialUpdate(Long id, Vehicle vehicle) {
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
                    if (!vehicle.getClients().isEmpty()) {
                        List<Long> existingClientsIds = existingVehicle.getClients().stream()
                                .map(Client::getId)
                                .toList();

                        vehicle.getClients().forEach(client -> {
                            if (!existingClientsIds.contains(client.getId())) {
                                Optional<Client> foundClient = clientRepository.findById(client.getId());
                                foundClient.ifPresent(existingVehicle.getClients()::add);
                            }
                        });
                    }

                    return vehicleRepository.save(existingVehicle);
                })
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        vehicleRepository.findById(id).ifPresent(vehicleRepository::delete);
    }
}
