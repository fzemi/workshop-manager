package com.fzemi.workshopmanager.repairparts.repository;

import com.fzemi.workshopmanager.repairparts.entity.RepairPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairPartRepository extends JpaRepository<RepairPart, Long> {
    List<RepairPart> findRepairPartsByRepairId(Long id);
}
