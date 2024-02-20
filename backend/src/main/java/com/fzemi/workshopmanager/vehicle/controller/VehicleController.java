package com.fzemi.workshopmanager.vehicle.controller;

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
    // TODO: add DTOs
    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> listVehicles() {
        return ResponseEntity.ok(vehicleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        Optional<Vehicle> foundVehicle = vehicleService.findVehicleById(id);
        return foundVehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/vin/{vin}")
    public ResponseEntity<Vehicle> getVehicleByVin(@PathVariable String vin) {
        Optional<Vehicle> foundVehicle = vehicleService.findVehicleByVin(vin);
        return foundVehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/plate/{plate}")
    public ResponseEntity<Vehicle> getVehicleByLicencePlate(@PathVariable String plate) {
        Optional<Vehicle> foundVehicle = vehicleService.findVehicleByLicencePlate(plate);
        return foundVehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        Vehicle createdVehicle = vehicleService.save(vehicle);
        return new ResponseEntity<>(createdVehicle, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> fullUpdateVehicle(
            @PathVariable Long id,
            @RequestBody Vehicle vehicle) {

        Optional<Vehicle> foundVehicle = vehicleService.findVehicleById(id);

        if (foundVehicle.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        vehicle.setId(id); // Without adding id Hibernate would create new instance instead of updating one
        Vehicle updatedVehicle = vehicleService.save(vehicle);
        return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Vehicle> partialUpdateVehicle(
            @PathVariable Long id,
            @RequestBody Vehicle vehicle) {

        Optional<Vehicle> foundVehicle = vehicleService.findVehicleById(id);

        if (foundVehicle.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Vehicle updatedVehicle = vehicleService.partialUpdate(id, vehicle);
        return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
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
