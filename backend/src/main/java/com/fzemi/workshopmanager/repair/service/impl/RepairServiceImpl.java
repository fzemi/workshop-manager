package com.fzemi.workshopmanager.repair.service.impl;

import com.fzemi.workshopmanager.repair.dto.RepairDTO;
import com.fzemi.workshopmanager.repair.dto.RepairMapper;
import com.fzemi.workshopmanager.repair.dto.RepairWithClientsDTO;
import com.fzemi.workshopmanager.repair.entity.Repair;
import com.fzemi.workshopmanager.repair.repository.RepairRepository;
import com.fzemi.workshopmanager.repair.service.RepairService;
import com.fzemi.workshopmanager.vehicle.entity.Vehicle;
import com.fzemi.workshopmanager.vehicle.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RepairServiceImpl implements RepairService {
    private final RepairRepository repairRepository;
    private final VehicleRepository vehicleRepository;
    private final RepairMapper repairMapper;

    @Autowired
    public RepairServiceImpl(
            RepairRepository repairRepository,
            VehicleRepository vehicleRepository,
            RepairMapper repairMapper) {
        this.repairRepository = repairRepository;
        this.vehicleRepository = vehicleRepository;
        this.repairMapper = repairMapper;
    }

    @Override
    public List<RepairDTO> findAll() {
        return repairRepository.findAll().stream()
                .map(repairMapper::toRepairDTO)
                .toList();
    }

    @Override
    public List<RepairWithClientsDTO> findAllWithClients() {
        return repairRepository.findAll().stream()
                .map(repairMapper::toRepairWithClientsDTO)
                .toList();
    }

    @Override
    public Optional<RepairDTO> findRepairById(Long id) {
        return repairRepository.findById(id).map(repairMapper::toRepairDTO);
    }

    @Override
    public Optional<RepairDTO> findRepairByNumber(String number) {
        return repairRepository.findByNumber(number).map(repairMapper::toRepairDTO);
    }

    @Override
    public RepairDTO save(Repair repair) {
        Repair createdRepair = repairRepository.save(repair);
        return repairMapper.toRepairDTO(createdRepair);
    }

    @Override
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
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        repairRepository.findById(id).ifPresent(repairRepository::delete);
    }
}
