package com.fzemi.workshopmanager.repair.service.impl;

import com.fzemi.workshopmanager.file.service.FileStorageService;
import com.fzemi.workshopmanager.repair.dto.RepairDTO;
import com.fzemi.workshopmanager.repair.dto.RepairMapper;
import com.fzemi.workshopmanager.repair.dto.RepairWithClientsDTO;
import com.fzemi.workshopmanager.repair.entity.Repair;
import com.fzemi.workshopmanager.repair.exception.RepairNotFoundException;
import com.fzemi.workshopmanager.repair.repository.RepairRepository;
import com.fzemi.workshopmanager.repair.service.RepairService;
import com.fzemi.workshopmanager.vehicle.entity.Vehicle;
import com.fzemi.workshopmanager.vehicle.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RepairServiceImpl implements RepairService {
    private final RepairRepository repairRepository;
    private final VehicleRepository vehicleRepository;
    private final RepairMapper repairMapper;
    private final FileStorageService fileStorageService;

    @Autowired
    public RepairServiceImpl(
            RepairRepository repairRepository,
            VehicleRepository vehicleRepository,
            RepairMapper repairMapper,
            FileStorageService fileStorageService) {
        this.repairRepository = repairRepository;
        this.vehicleRepository = vehicleRepository;
        this.repairMapper = repairMapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public List<RepairDTO> findAll() {
        return repairRepository.findAll().stream()
                .map(repairMapper::toRepairDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairWithClientsDTO> findAllWithClients() {
        return repairRepository.findAll().stream()
                .map(repairMapper::toRepairWithClientsDTO)
                .toList();
    }

    @Override
    public RepairDTO findRepairById(Long id) {
        return repairRepository.findById(id)
                .map(repairMapper::toRepairDTO)
                .orElseThrow(() -> new RepairNotFoundException("Repair with id: " + id + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public RepairWithClientsDTO findRepairByIdWithClients(Long id) {
        return repairRepository.findById(id)
                .map(repairMapper::toRepairWithClientsDTO)
                .orElseThrow(() -> new RepairNotFoundException("Repair with id: " + id + " not found"));
    }

    @Override
    public RepairDTO findRepairByNumber(String number) {
        return repairRepository.findByNumber(number)
                .map(repairMapper::toRepairDTO)
                .orElseThrow(() -> new RepairNotFoundException("Repair with number: " + number + " not found"));
    }

    @Override
    @Transactional
    public RepairDTO save(Repair repair) {
        // Look up existing vehicle and assign it to the repair
        if (repair.getVehicle() != null && repair.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(repair.getVehicle().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Vehicle with id: " + repair.getVehicle().getId() + " not found"));
            repair.setVehicle(vehicle);
        }

        Repair createdRepair = repairRepository.save(repair);
        return repairMapper.toRepairDTO(createdRepair);
    }

    @Override
    @Transactional
    public RepairDTO partialUpdate(Long id, Repair repair) {
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

                    return repairRepository.save(existingRepair);
                })
                .map(repairMapper::toRepairDTO)
                .orElseThrow(() -> new RepairNotFoundException("Cannot update repair with id: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repairRepository.existsById(id)) {
            throw new RepairNotFoundException("Cannot delete repair with id: " + id);
        }

        fileStorageService.deleteAllByRepairId(id);
        repairRepository.deleteById(id);
    }
}
