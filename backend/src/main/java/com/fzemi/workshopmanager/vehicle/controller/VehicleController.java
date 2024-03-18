package com.fzemi.workshopmanager.vehicle.controller;

import com.fzemi.workshopmanager.vehicle.dto.VehicleDTO;
import com.fzemi.workshopmanager.vehicle.entity.Vehicle;
import com.fzemi.workshopmanager.vehicle.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;
    
    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<List<VehicleDTO>> listVehicles() {
        return ResponseEntity.ok(vehicleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable Long id) {
        Optional<VehicleDTO> foundVehicle = vehicleService.findVehicleById(id);
        return foundVehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/vin/{vin}")
    public ResponseEntity<VehicleDTO> getVehicleByVin(@PathVariable String vin) {
        Optional<VehicleDTO> foundVehicle = vehicleService.findVehicleByVin(vin);
        return foundVehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/plate/{plate}")
    public ResponseEntity<VehicleDTO> getVehicleByLicencePlate(@PathVariable String plate) {
        Optional<VehicleDTO> foundVehicle = vehicleService.findVehicleByLicencePlate(plate);
        return foundVehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VehicleDTO> createVehicle(@RequestBody Vehicle vehicle) {
        VehicleDTO createdVehicleDTO = vehicleService.save(vehicle);
        return new ResponseEntity<>(createdVehicleDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> fullUpdateVehicle(
            @PathVariable Long id,
            @RequestBody Vehicle vehicle) {

        Optional<VehicleDTO> foundVehicleDTO = vehicleService.findVehicleById(id);

        if (foundVehicleDTO.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        vehicle.setId(id); // Without adding id Hibernate would create new instance instead of updating one
        VehicleDTO updatedVehicleDTO = vehicleService.save(vehicle);
        return new ResponseEntity<>(updatedVehicleDTO, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VehicleDTO> partialUpdateVehicle(
            @PathVariable Long id,
            @RequestBody Vehicle vehicle) {

        Optional<VehicleDTO> foundVehicle = vehicleService.findVehicleById(id);

        if (foundVehicle.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        VehicleDTO updatedVehicleDTO = vehicleService.partialUpdate(id, vehicle);
        return new ResponseEntity<>(updatedVehicleDTO, HttpStatus.OK);
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity deleteVehicle(@PathVariable Long id) {
//        Optional<Vehicle> foundVehicle = vehicleService.findVehicleById(id);
//
//        if(foundVehicle.isEmpty()) {
//            return new ResponseEntity(HttpStatus.NOT_FOUND);
//        }
//
//        vehicleService.delete(id);
//        return new ResponseEntity(HttpStatus.NO_CONTENT);
//    }
}
