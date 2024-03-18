package com.fzemi.workshopmanager.vehicle.service;

import com.fzemi.workshopmanager.vehicle.dto.VehicleDTO;
import com.fzemi.workshopmanager.vehicle.entity.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleService {

    /**
     * @return all vehicleDTOs
     */
    List<VehicleDTO> findAll();

    /**
     * @param id vehicle's id
     * @return vehicleDTO with the given id
     */
    Optional<VehicleDTO> findVehicleById(Long id);

    /**
     * @param vin vehicle's VIN
     * @return vehicleDTO with the given VIN
     */
    Optional<VehicleDTO> findVehicleByVin(String vin);

    /**
     * @param licencePlate vehicle's licence plate
     * @return vehicleDTO with the given licence plate
     */
    Optional<VehicleDTO> findVehicleByLicencePlate(String licencePlate);

    /**
     * Creates a new vehicle or fully updates an existing one
     *
     * @param vehicle new vehicle
     * @return created vehicleDTO
     */
    VehicleDTO save(Vehicle vehicle);

    /**
     * Partially updates an existing vehicle
     *
     * @param id      existing vehicle's id
     * @param vehicle vehicle with updated fields
     * @return updated vehicleDTO
     */
    VehicleDTO partialUpdate(Long id, Vehicle vehicle);

    /**
     * Deletes existing vehicle
     *
     * @param id existing vehicle's id to be deleted
     */
    void delete(Long id);
}
