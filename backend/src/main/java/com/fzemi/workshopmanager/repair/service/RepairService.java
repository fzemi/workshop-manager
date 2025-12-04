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
     * @throws com.fzemi.workshopmanager.repair.exception.RepairNotFoundException if repair with the given id does not exist
     */
    RepairDTO findRepairById(Long id);

    /**
     * @param id repair's id
     * @return repair with the given id including associated clients
     * @throws com.fzemi.workshopmanager.repair.exception.RepairNotFoundException if repair with the given id does not exist
     */
    RepairWithClientsDTO findRepairByIdWithClients(Long id);

    /**
     * @param number repair's number
     * @return repair with the given repair number
     * @throws com.fzemi.workshopmanager.repair.exception.RepairNotFoundException if repair with the given number does not exist
     */
    RepairDTO findRepairByNumber(String number);

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
     * @throws com.fzemi.workshopmanager.repair.exception.RepairNotFoundException if repair with the given id does not exist
     */
    RepairDTO partialUpdate(Long id, Repair repair);

    /**
     * Deletes existing repair
     *
     * @param id existing repair's id to be deleted
     * @throws com.fzemi.workshopmanager.repair.exception.RepairNotFoundException if repair with the given id does not exist
     */
    void delete(Long id);
}
