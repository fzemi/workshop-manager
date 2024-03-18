package com.fzemi.workshopmanager.repair.service;

import com.fzemi.workshopmanager.repair.dto.RepairDTO;
import com.fzemi.workshopmanager.repair.dto.RepairWithClientsDTO;
import com.fzemi.workshopmanager.repair.entity.Repair;

import java.util.List;
import java.util.Optional;

public interface RepairService {

    /**
     * @return all repairs
     */
    List<RepairDTO> findAll();

    /**
     * @return all repairs with associated clients
     */
    List<RepairWithClientsDTO> findAllWithClients();

    /**
     * @param id repair's id
     * @return repair with the given id
     */
    Optional<RepairDTO> findRepairById(Long id);

    /**
     * @param number repair's number
     * @return repair with the given repair number
     */
    Optional<RepairDTO> findRepairByNumber(String number);

    /**
     * Creates a new repair or fully updates an existing one
     *
     * @param repair new repair
     * @return created repair
     */
    RepairDTO save(Repair repair);

    /**
     * Partially updates an existing repair
     *
     * @param id     existing repair's id
     * @param repair repair with updated fields
     * @return updated repair
     */
    RepairDTO partialUpdate(Long id, Repair repair);

    /**
     * Deletes existing repair
     *
     * @param id existing repair's id to be deleted
     */
    void delete(Long id);
}
