package com.fzemi.workshopmanager.repairparts.service.impl;

import com.fzemi.workshopmanager.part.entity.Part;
import com.fzemi.workshopmanager.part.repository.PartRepository;
import com.fzemi.workshopmanager.repair.entity.Repair;
import com.fzemi.workshopmanager.repair.exception.RepairNotFoundException;
import com.fzemi.workshopmanager.repair.repository.RepairRepository;
import com.fzemi.workshopmanager.repairparts.dto.RepairPartDTO;
import com.fzemi.workshopmanager.repairparts.dto.RepairPartMapper;
import com.fzemi.workshopmanager.repairparts.entity.RepairPart;
import com.fzemi.workshopmanager.repairparts.exception.RepairPartNotFoundException;
import com.fzemi.workshopmanager.repairparts.repository.RepairPartRepository;
import com.fzemi.workshopmanager.repairparts.service.RepairPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RepairPartServiceImpl implements RepairPartService {

    private final RepairPartMapper repairPartMapper;
    private final RepairPartRepository repairPartRepository;
    private final RepairRepository repairRepository;
    private final PartRepository partRepository;

    // TODO: Change repositories to services for different entities than each service is responsible for
    @Autowired
    public RepairPartServiceImpl(
            RepairPartMapper repairPartMapper,
            RepairPartRepository repairPartRepository,
            RepairRepository repairRepository,
            PartRepository partRepository
    ) {
        this.repairPartMapper = repairPartMapper;
        this.repairPartRepository = repairPartRepository;
        this.repairRepository = repairRepository;
        this.partRepository = partRepository;
    }

    @Override
    public List<RepairPartDTO> findAll() {
        return repairPartRepository.findAll().stream()
                .map(repairPartMapper::toRepairPartDTO)
                .toList();
    }

    @Override
    public RepairPartDTO findRepairPartById(Long id) {
        return repairPartRepository.findById(id)
                .map(repairPartMapper::toRepairPartDTO)
                .orElseThrow(() -> new RepairPartNotFoundException("Repair part with id: " + id + " not found"));
    }

    @Override
    public List<RepairPartDTO> findRepairPartsByRepairId(Long id) {
        if (!repairRepository.existsById(id)) {
            throw new RepairNotFoundException("Repair with id: " + id + " not found");
        }

        return repairPartRepository.findRepairPartsByRepairId(id).stream()
                .map(repairPartMapper::toRepairPartDTO)
                .toList();
    }

    @Override
    public RepairPartDTO save(RepairPart repairPart) {
        return repairPartMapper.toRepairPartDTO(repairPartRepository.save(repairPart));
    }

    @Override
    @Transactional
    public RepairPartDTO partialUpdate(Long id, RepairPart repairPart) {
        return repairPartRepository.findById(id).map(existingRepairPart -> {
                    Optional.ofNullable(repairPart.getWorkType()).ifPresent(existingRepairPart::setWorkType);
                    Optional.ofNullable(repairPart.getQuantity()).ifPresent(existingRepairPart::setQuantity);

                    // look for existing repair and assign it to the repairPart
                    if (repairPart.getRepair() != null && repairPart.getRepair().getId() != null) {
                        Optional<Repair> foundRepair = repairRepository.findById(repairPart.getRepair().getId());

                        foundRepair.ifPresent(existingRepairPart::setRepair);
                    }

                    // look for existing part and assign it to the repairPart
                    if (repairPart.getPart() != null && repairPart.getPart().getId() != null) {
                        Optional<Part> foundPart = partRepository.findById(repairPart.getPart().getId());

                        foundPart.ifPresent(existingRepairPart::setPart);
                    }

                    return repairPartRepository.save(existingRepairPart);
                })
                .map(repairPartMapper::toRepairPartDTO)
                .orElseThrow(() -> new RepairPartNotFoundException("Cannot update repair part with id: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repairPartRepository.existsById(id)) {
            throw new RepairPartNotFoundException("Cannot delete repair part with id: " + id);
        }

        repairPartRepository.deleteById(id);
    }
}
