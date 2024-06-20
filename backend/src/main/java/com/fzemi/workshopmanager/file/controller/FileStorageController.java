package com.fzemi.workshopmanager.file.controller;

import com.fzemi.workshopmanager.file.dto.FileDTO;
import com.fzemi.workshopmanager.file.dto.FileMapper;
import com.fzemi.workshopmanager.file.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/repairs/{repairId}/files")
public class FileStorageController {
    private final FileStorageService fileStorageService;
    private final FileMapper fileMapper;

    @Autowired
    public FileStorageController(
            FileStorageService fileStorageService,
            FileMapper fileMapper
    ) {
        this.fileStorageService = fileStorageService;
        this.fileMapper = fileMapper;
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> getFile(
            @PathVariable Long repairId,
            @PathVariable Long fileId
    ) {
        Resource file = fileStorageService.loadAsResource(fileId);
        return ResponseEntity.ok(file);
    }

    @GetMapping
    public ResponseEntity<List<FileDTO>> getAllRepairFiles(@PathVariable Long repairId) {
        List<FileDTO> filesDTOs = fileStorageService.loadAllRepairFiles(repairId).stream()
                .map(fileMapper::toFileDTO)
                .toList();
        return ResponseEntity.ok(filesDTOs);
    }

    @PostMapping
    public ResponseEntity uploadFile(
            @PathVariable Long repairId,
            @RequestParam("tags") String tags,
            @RequestParam("file") MultipartFile file
    ) {
        fileStorageService.uploadFile(repairId, file, tags);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity deleteFile(
            @PathVariable Long repairId,
            @PathVariable Long fileId
    ) {
        fileStorageService.delete(fileId);
        return ResponseEntity.noContent().build();
    }
}
