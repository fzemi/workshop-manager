package com.fzemi.workshopmanager.repair.repository;

import com.fzemi.workshopmanager.repair.entity.Repair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepairRepository extends JpaRepository<Repair, Long> {
    Optional<Repair> findByNumber(String number);
}
