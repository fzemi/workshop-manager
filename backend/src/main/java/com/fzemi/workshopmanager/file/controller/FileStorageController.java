package com.fzemi.workshopmanager.file.controller;

import com.fzemi.workshopmanager.file.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/repairs/{repairId}/files")
public class FileStorageController {
    private final FileStorageService fileStorageService;

    @Autowired
    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
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
    public ResponseEntity getAllFiles(@PathVariable Long repairId) {
        return ResponseEntity.ok(fileStorageService.loadAllRepairFiles(repairId));
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
