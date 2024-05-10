package com.fzemi.workshopmanager.repairparts.dto;

import com.fzemi.workshopmanager.part.dto.PartDTO;
import com.fzemi.workshopmanager.repairparts.config.WorkTypeFormat;
import lombok.Builder;

@Builder
public record RepairPartDTO(
        Long id,
        Long repairID,
        PartDTO part,
        WorkTypeFormat workType,
        Integer quantity
) {
}
