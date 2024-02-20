package com.fzemi.workshopmanager.vehicle.service;

import com.fzemi.workshopmanager.vehicle.entity.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleService {

    /**
     * @return all vehicles
     */
    List<Vehicle> findAll();

    /**
     * @param id vehicle's id
     * @return vehicle with the given id
     */
    Optional<Vehicle> findVehicleById(Long id);

    /**
     * @param vin vehicle's VIN
     * @return vehicle with the given VIN
     */
    Optional<Vehicle> findVehicleByVin(String vin);

    /**
     * @param licencePlate vehicle's licence plate
     * @return vehicle with the given licence plate
     */
    Optional<Vehicle> findVehicleByLicencePlate(String licencePlate);

    /**
     * Creates a new vehicle or fully updates an existing one
     *
     * @param vehicle new vehicle
     * @return created vehicle
     */
    Vehicle save(Vehicle vehicle);

    /**
     * Partially updates an existing vehicle
     *
     * @param id      existing vehicle's id
     * @param vehicle vehicle with updated fields
     * @return updated vehicle
     */
    Vehicle partialUpdate(Long id, Vehicle vehicle);

    /**
     * Deletes existing vehicle
     *
     * @param id existing vehicle's id to be deleted
     */
    void delete(Long id);
}
