package com.fzemi.workshopmanager.file.service.impl;

import com.fzemi.workshopmanager.file.config.FileTags;
import com.fzemi.workshopmanager.file.config.LocalFileStorageConfig;
import com.fzemi.workshopmanager.file.entity.File;
import com.fzemi.workshopmanager.file.exception.FileUploadException;
import com.fzemi.workshopmanager.file.repository.FileRepository;
import com.fzemi.workshopmanager.file.service.FileStorageService;
import com.fzemi.workshopmanager.handler.ErrorCodes;
import com.fzemi.workshopmanager.repair.entity.Repair;
import com.fzemi.workshopmanager.repair.repository.RepairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LocalFileStorageServiceImpl implements FileStorageService {

    private final Path rootLocation;
    private final String baseFileUrl;
    private final RepairRepository repairRepository;
    private final FileRepository fileRepository;

    @Autowired
    public LocalFileStorageServiceImpl(
            LocalFileStorageConfig properties,
            RepairRepository repairRepository,
            FileRepository fileRepository) {
        if (properties.getLocation().trim().isEmpty()) {
            throw new FileUploadException("File upload location not set", ErrorCodes.FILE_NOT_UPLOADED);
        }

        this.rootLocation = Paths.get(properties.getLocation());
        this.baseFileUrl = properties.getBaseUrl();
        this.repairRepository = repairRepository;
        this.fileRepository = fileRepository;
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new FileUploadException("Could not initialize storage", ErrorCodes.FILE_NOT_UPLOADED, e);
        }
    }

    @Override
    @Transactional
    public void uploadFile(
            Long repairId,
            MultipartFile file
    ) {
        try {
            if (file.isEmpty()) {
                throw new FileUploadException("Failed to store empty file: " + file.getOriginalFilename(), ErrorCodes.FILE_NOT_UPLOADED);
            }

            Optional<Repair> repair = repairRepository.findById(repairId);

            if (repair.isEmpty()) {
                throw new FileUploadException("Failed to upload file: " + file.getOriginalFilename() + " for non-existing repair", ErrorCodes.FILE_NOT_UPLOADED);
            }

            Path destinationFile = rootLocation.resolve(Paths.get(
                            Objects.requireNonNull(file.getOriginalFilename())))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
                throw new FileUploadException("Cannot store file outside current directory", ErrorCodes.FILE_NOT_UPLOADED);
            }

            Repair repairEntity = repair.get();
            String filename = destinationFile.getFileName().toString();
            String fileUrl = baseFileUrl + rootLocation + "/" + repairEntity.getNumber() + "/" + filename;
            Path newFilePath = Paths.get(destinationFile.getParent() + "/" + repairEntity.getNumber() + "/" + filename);

            // Auto-detect file type based on content type
            List<FileTags> fileTags = detectFileTags(file.getContentType());

            Files.createDirectories(newFilePath.getParent());

            File fileToSave = File.builder()
                    .filename(filename)
                    .contentType(file.getContentType())
                    .filePath(newFilePath.toString())
                    .fileUrl(fileUrl)
                    .size(file.getSize())
                    .tags(fileTags)
                    .repair(repairEntity)
                    .build();

            fileRepository.save(fileToSave);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, newFilePath);
            }
        } catch (FileAlreadyExistsException e) {
            throw new FileUploadException("File already exists: " + file.getOriginalFilename(), ErrorCodes.FILE_ALREADY_EXISTS, e);
        } catch (IOException e) {
            throw new FileUploadException("Failed to store file: " + file.getOriginalFilename(), ErrorCodes.FILE_NOT_UPLOADED, e);
        }
    }

    /**
     * Auto-detect file tags based on content type.
     * Images (image/*) get IMAGE tag, all others get DOCUMENT tag.
     */
    private List<FileTags> detectFileTags(String contentType) {
        if (contentType != null && contentType.startsWith("image/")) {
            return List.of(FileTags.IMAGE);
        }
        return List.of(FileTags.DOCUMENT);
    }

    @Override
    public File save(File file) {
        return fileRepository.save(file);
    }

    @Override
    public File getFileById(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new FileUploadException("File not found with id: " + fileId, ErrorCodes.FILE_NOT_FOUND));
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(Long fileId) {
        try {
            File file = fileRepository.findById(fileId)
                    .orElseThrow(() -> new FileUploadException("File not found with id: " + fileId, ErrorCodes.FILE_NOT_FOUND));

            String filePath = file.getFilePath();
            Path fileLocation = load(filePath);
            Resource resource = new UrlResource(fileLocation.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileUploadException("Could not read file: " + file.getFilename(), ErrorCodes.NO_FILE_ACCESS);
            }
        } catch (MalformedURLException e) {
            throw new FileUploadException("Failed to read file with id: " + fileId, ErrorCodes.NO_FILE_ACCESS, e);
        }
    }

    @Override
    public List<File> loadAllRepairFiles(Long repairId) {
        List<File> repairFiles = fileRepository.findAllByRepairId(repairId);
        return repairFiles;
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
        fileRepository.deleteAll();
    }

    @Override
    @Transactional
    public void deleteAllByRepairId(Long repairId) {
        List<File> files = fileRepository.findAllByRepairId(repairId);

        files.forEach(file -> {
            delete(file.getId());
        });
    }

    @Override
    @Transactional
    public void delete(Long fileId) {
        try {
            File file = fileRepository.findById(fileId)
                    .orElseThrow(() -> new FileUploadException("Failed to delete file with id: " + fileId, ErrorCodes.FILE_NOT_FOUND));

            String filePath = file.getFilePath();

            FileSystemUtils.deleteRecursively(load(filePath));
            fileRepository.deleteById(fileId);
        } catch (IOException e) {
            throw new FileUploadException("Failed to delete file: " + fileId, ErrorCodes.FILE_NOT_UPLOADED, e);
        }
    }
}
