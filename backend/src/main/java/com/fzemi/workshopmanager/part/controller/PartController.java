package com.fzemi.workshopmanager.part.controller;

import com.fzemi.workshopmanager.part.dto.PartDTO;
import com.fzemi.workshopmanager.part.entity.Part;
import com.fzemi.workshopmanager.part.service.PartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parts")
public class PartController {
    private final PartService partService;

    @Autowired
    public PartController(PartService partService) {
        this.partService = partService;
    }

    @GetMapping
    public ResponseEntity<List<PartDTO>> listParts() {
        return ResponseEntity.ok(partService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartDTO> getPartById(@PathVariable Long id) {
        PartDTO foundPartDTO = partService.findPartById(id);
        return ResponseEntity.ok(foundPartDTO);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<PartDTO>> listPartsByFilter(
            @RequestParam(required = false) String partName,
            @RequestParam(required = false) String serialNumber,
            @RequestParam(required = false) String manufacturer
    ) {
        return ResponseEntity.ok(partService.findPartsByFilter(partName, serialNumber, manufacturer));
    }

    @PostMapping
    public ResponseEntity<PartDTO> createPart(@RequestBody Part part) {
        PartDTO createdPartDTO = partService.save(part);
        return new ResponseEntity<>(createdPartDTO, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PartDTO> partialUpdatePart(
            @PathVariable Long id,
            @RequestBody Part part
    ) {
        PartDTO updatedPartDTO = partService.partialUpdate(id, part);
        return new ResponseEntity<>(updatedPartDTO, HttpStatus.OK);
    }

}
