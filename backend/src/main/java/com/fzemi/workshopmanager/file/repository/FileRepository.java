package com.fzemi.workshopmanager.file.repository;

import com.fzemi.workshopmanager.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findAllByRepairId(Long repairId);
}
