package com.fzemi.workshopmanager.repairparts.service;

import com.fzemi.workshopmanager.repairparts.dto.RepairPartDTO;
import com.fzemi.workshopmanager.repairparts.entity.RepairPart;

import java.util.List;
import java.util.Optional;

public interface RepairPartService {

    /**
     * @return all repair parts
     */
    List<RepairPartDTO> findAll();

    /**
     * @param id repair part's id
     * @return repair part with the given id
     * @throws com.fzemi.workshopmanager.repairparts.exception.RepairPartNotFoundException if repair part with given id does not exist
     */
    RepairPartDTO findRepairPartById(Long id);

    /**
     * @param id repair id
     * @return all repair parts from given repair
     */
    List<RepairPartDTO> findRepairPartsByRepairId(Long id);

    /**
     * Creates a new repair part or fully updates an exisiting one
     *
     * @param repairPart new repair part
     * @return created repair part
     */
    RepairPartDTO save(RepairPart repairPart);

    /**
     * Partially updates an existing repair part
     *
     * @param id         existing repair part's id
     * @param repairPart repair part with updated fields
     * @return updated repair part
     * @throws com.fzemi.workshopmanager.repairparts.exception.RepairPartNotFoundException if repair part with given id does not exist
     */
    RepairPartDTO partialUpdate(Long id, RepairPart repairPart);

    /**
     * Deletes existing repair part
     *
     * @param id exisiting repair part's id to be deleted
     * @throws com.fzemi.workshopmanager.repairparts.exception.RepairPartNotFoundException if repair part with given id does not exist
     */
    void delete(Long id);
}
