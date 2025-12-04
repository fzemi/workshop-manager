package com.fzemi.workshopmanager.file.service;

import com.fzemi.workshopmanager.file.entity.File;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface FileStorageService {
    void init();

    void uploadFile(Long repairId, MultipartFile file);

    File save(File file);

    File getFileById(Long fileId);

    Path load(String filename);

    Resource loadAsResource(Long fileId);

    List<File> loadAllRepairFiles(Long repairId);

    void deleteAll();

    void deleteAllByRepairId(Long repairId);

    void delete(Long fileId);
}
