package com.fzemi.workshopmanager.part.service;

import com.fzemi.workshopmanager.part.dto.PartDTO;
import com.fzemi.workshopmanager.part.entity.Part;

import java.util.List;

public interface PartService {

    /**
     * @return all parts
     */
    List<PartDTO> findAll();

    /**
     * @param id part's id
     * @return part with the given id
     * @throws com.fzemi.workshopmanager.part.exception.PartNotFoundException if part with given id does not exist
     */
    PartDTO findPartById(Long id);

    /**
     * @param partName     part's name
     * @param serialNumber part's serial number
     * @param manufacturer part's manufacturer
     * @return all parts by provided fields
     */
    List<PartDTO> findPartsByFilter(
            String partName,
            String serialNumber,
            String manufacturer);

    /**
     * Creates a new part or fully updates an existing one
     *
     * @param part new part
     * @return created part
     */
    PartDTO save(Part part);

    /**
     * Partially updates an existing part
     *
     * @param id   existing part's id
     * @param part part with updated fields
     * @return updated part
     * @throws com.fzemi.workshopmanager.part.exception.PartNotFoundException if part with given id does not exist
     */
    PartDTO partialUpdate(Long id, Part part);
}
