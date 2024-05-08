package com.fzemi.workshopmanager.part.service.impl;

import com.fzemi.workshopmanager.part.dto.PartDTO;
import com.fzemi.workshopmanager.part.dto.PartMapper;
import com.fzemi.workshopmanager.part.entity.Part;
import com.fzemi.workshopmanager.part.entity.PartSpecification;
import com.fzemi.workshopmanager.part.repository.PartRepository;
import com.fzemi.workshopmanager.part.service.PartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PartServiceImpl implements PartService {
    private final PartRepository partRepository;
    private final PartMapper partMapper;

    @Autowired
    public PartServiceImpl(
            PartRepository partRepository,
            PartMapper partMapper
    ) {
        this.partRepository = partRepository;
        this.partMapper = partMapper;
    }

    public List<PartDTO> findAll() {
        return partRepository.findAll().stream()
                .map(partMapper::toPartDTO)
                .toList();
    }

    public Optional<PartDTO> findPartById(Long id) {
        return partRepository.findById(id).map(partMapper::toPartDTO);
    }

    public List<PartDTO> findPartsByFilter(
            String partName,
            String serialNumber,
            String manufacturer
    ) {
        Specification<Part> specification = PartSpecification.filterPart(partName, serialNumber, manufacturer);
        return partRepository.findAll(specification).stream()
                .map(partMapper::toPartDTO)
                .toList();
    }

    public PartDTO save(Part part) {
        return partMapper.toPartDTO(partRepository.save(part));
    }

    public PartDTO partialUpdate(Long id, Part part) {
        return partRepository.findById(id).map(existingPart -> {
                    Optional.ofNullable(part.getPartName()).ifPresent(existingPart::setPartName);
                    Optional.ofNullable(part.getSerialNumber()).ifPresent(existingPart::setSerialNumber);
                    Optional.ofNullable(part.getManufacturer()).ifPresent(existingPart::setManufacturer);

                    return partRepository.save(existingPart);
                })
                .map(partMapper::toPartDTO)
                .orElse(null);
    }
}
