package com.fzemi.workshopmanager.file.dto;

import com.fzemi.workshopmanager.file.entity.File;
import org.springframework.stereotype.Component;

@Component
public class FileMapper {

    public FileDTO toFileDTO(File file) {
        return FileDTO.builder()
                .id(file.getId())
                .repairId(file.getRepair().getId())
                .filename(file.getFilename())
                .contentType(file.getContentType())
                .fileUrl(file.getFileUrl())
                .size(file.getSize())
                .tags(file.getTags())
                .build();
    }
}
