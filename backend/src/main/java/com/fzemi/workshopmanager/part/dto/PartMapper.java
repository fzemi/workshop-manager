package com.fzemi.workshopmanager.part.dto;

import com.fzemi.workshopmanager.part.entity.Part;
import org.springframework.stereotype.Component;

@Component
public class PartMapper {

    public PartDTO toPartDTO(Part part) {
        return PartDTO.builder()
                .id(part.getId())
                .partName(part.getPartName())
                .serialNumber(part.getSerialNumber())
                .manufacturer(part.getManufacturer())
                .build();
    }
}
