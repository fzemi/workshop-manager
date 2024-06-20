package com.fzemi.workshopmanager.file.dto;

import com.fzemi.workshopmanager.file.config.FileTags;
import lombok.Builder;

import java.util.List;

@Builder
public record FileDTO(
        Long id,
        Long repairId,
        String filename,
        String contentType,
        String fileUrl,
        Long size,
        List<FileTags> tags
) {
}
