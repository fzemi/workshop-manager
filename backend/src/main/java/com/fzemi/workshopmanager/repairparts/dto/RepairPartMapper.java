package com.fzemi.workshopmanager.repairparts.dto;

import com.fzemi.workshopmanager.part.dto.PartDTO;
import com.fzemi.workshopmanager.part.dto.PartMapper;
import com.fzemi.workshopmanager.repairparts.entity.RepairPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepairPartMapper {

    private final PartMapper partMapper;

    @Autowired
    public RepairPartMapper(PartMapper partMapper) {
        this.partMapper = partMapper;
    }

    public RepairPartDTO toRepairPartDTO(RepairPart repairPart) {
        PartDTO part = repairPart.getPart() != null ?
                partMapper.toPartDTO(repairPart.getPart()) : null;


        return RepairPartDTO.builder()
                .id(repairPart.getId())
                .part(part)
                .repairID(repairPart.getRepair().getId())
                .workType(repairPart.getWorkType())
                .quantity(repairPart.getQuantity())
                .build();
    }
}
