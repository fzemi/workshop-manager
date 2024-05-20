package com.fzemi.workshopmanager.vehicle.service;

import com.fzemi.workshopmanager.vehicle.dto.VehicleDTO;
import com.fzemi.workshopmanager.vehicle.entity.Vehicle;

import java.util.List;

public interface VehicleService {

    /**
     * @return all vehicleDTOs
     */
    List<VehicleDTO> findAll();

    /**
     * @param id vehicle's id
     * @return vehicleDTO with the given id
     * @throws com.fzemi.workshopmanager.vehicle.exception.VehicleNotFoundException if vehicle with given id does not exist
     */
    VehicleDTO findVehicleById(Long id);

    /**
     * @param vin vehicle's VIN
     * @return vehicleDTO with the given VIN
     * @throws com.fzemi.workshopmanager.vehicle.exception.VehicleNotFoundException if vehicle with given id does not exist
     */
    VehicleDTO findVehicleByVin(String vin);

    /**
     * @param licencePlate vehicle's licence plate
     * @return vehicleDTO with the given licence plate
     * @throws com.fzemi.workshopmanager.vehicle.exception.VehicleNotFoundException if vehicle with given id does not exist
     */
    VehicleDTO findVehicleByLicencePlate(String licencePlate);

    /**
     * Creates a new vehicle
     *
     * @param vehicle new vehicle
     * @return created vehicleDTO
     */
    VehicleDTO save(Vehicle vehicle);

    /**
     * Fully updates an existing vehicle
     *
     * @param vehicle vehicle with updated fields
     * @return updated vehicleDTO
     * @throws com.fzemi.workshopmanager.vehicle.exception.VehicleNotFoundException if vehicle with given id does not exist
     */
    VehicleDTO fullUpdate(Vehicle vehicle);

    /**
     * Partially updates an existing vehicle
     *
     * @param id      existing vehicle's id
     * @param vehicle vehicle with updated fields
     * @return updated vehicleDTO
     * @throws com.fzemi.workshopmanager.vehicle.exception.VehicleNotFoundException if vehicle with given id does not exist
     */
    VehicleDTO partialUpdate(Long id, Vehicle vehicle);

    /**
     * Deletes existing vehicle
     *
     * @param id existing vehicle's id to be deleted
     * @throws com.fzemi.workshopmanager.vehicle.exception.VehicleNotFoundException if vehicle with given id does not exist
     */
    void delete(Long id);
}
