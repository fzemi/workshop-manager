package com.fzemi.workshopmanager.repair.service.impl;

import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.client.repository.ClientRepository;
import com.fzemi.workshopmanager.repair.entity.Repair;
import com.fzemi.workshopmanager.repair.repository.RepairRepository;
import com.fzemi.workshopmanager.repair.service.RepairService;
import com.fzemi.workshopmanager.vehicle.entity.Vehicle;
import com.fzemi.workshopmanager.vehicle.repository.VehicleRepository;
import com.fzemi.workshopmanager.vehicle.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RepairServiceImpl implements RepairService {
    private final RepairRepository repairRepository;
    private final VehicleRepository vehicleRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public RepairServiceImpl(
            RepairRepository repairRepository,
            VehicleRepository vehicleRepository,
            ClientRepository clientRepository) {
        this.repairRepository = repairRepository;
        this.vehicleRepository = vehicleRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public List<Repair> findAll() {
        return repairRepository.findAll();
    }

    @Override
    public Optional<Repair> findRepairById(Long id) {
        return repairRepository.findById(id);
    }

    @Override
    public Optional<Repair> findRepairByNumber(String number) {
        return repairRepository.findByNumber(number);
    }

    @Override
    public Repair save(Repair repair) {
        return repairRepository.save(repair);
    }

    @Override
    public Repair partialUpdate(Long id, Repair repair) {
        return repairRepository.findById(id).map(existingRepair -> {
                    Optional.ofNullable(repair.getNumber()).ifPresent(existingRepair::setNumber);
                    Optional.ofNullable(repair.getStartDate()).ifPresent(existingRepair::setStartDate);
                    Optional.ofNullable(repair.getExpectedEndDate()).ifPresent(existingRepair::setExpectedEndDate);
                    Optional.ofNullable(repair.getType()).ifPresent(existingRepair::setType);

                    // look for existing vehicle and assign it to the repair
                    if (repair.getVehicle() != null && repair.getVehicle().getId() != null) {
                        Optional<Vehicle> foundVehicle = vehicleRepository.findById(repair.getVehicle().getId());

                        foundVehicle.ifPresent(existingRepair::setVehicle);
                    }

                    // look for existing client and assign it to the repair
                    if (repair.getClient() != null && repair.getClient().getId() != null) {
                        Optional<Client> foundClient = clientRepository.findById(repair.getClient().getId());

                        foundClient.ifPresent(existingRepair::setClient);
                    }

                    return repairRepository.save(existingRepair);
                })
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        repairRepository.findById(id).ifPresent(repairRepository::delete);
    }
}
