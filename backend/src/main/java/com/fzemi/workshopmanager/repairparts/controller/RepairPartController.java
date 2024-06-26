package com.fzemi.workshopmanager.repairparts.controller;

import com.fzemi.workshopmanager.repair.service.RepairService;
import com.fzemi.workshopmanager.repairparts.dto.RepairPartDTO;
import com.fzemi.workshopmanager.repairparts.entity.RepairPart;
import com.fzemi.workshopmanager.repairparts.service.RepairPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RepairPartController {
    private final RepairPartService repairPartService;
    private final RepairService repairService;

    @Autowired
    public RepairPartController(
            RepairPartService repairPartService,
            RepairService repairService
    ) {
        this.repairPartService = repairPartService;
        this.repairService = repairService;
    }

    @GetMapping("/repair-parts")
    public ResponseEntity<List<RepairPartDTO>> listRepairParts() {
        return ResponseEntity.ok(repairPartService.findAll());
    }

    @GetMapping("/repair-parts/{id}")
    public ResponseEntity<RepairPartDTO> getRepairPartById(@PathVariable Long id) {
        RepairPartDTO foundRepairPart = repairPartService.findRepairPartById(id);
        return ResponseEntity.ok(foundRepairPart);
    }

    @GetMapping("/repairs/{id}/repair-parts")
    public ResponseEntity<List<RepairPartDTO>> listRepairPartsFromRepair(@PathVariable Long id) {
        return ResponseEntity.ok(repairPartService.findRepairPartsByRepairId(id));
    }

    @PostMapping("/repair-parts")
    public ResponseEntity<RepairPartDTO> createRepairPart(@RequestBody RepairPart repairPart) {
        RepairPartDTO createdRepairPartDTO = repairPartService.save(repairPart);
        return new ResponseEntity<>(createdRepairPartDTO, HttpStatus.CREATED);
    }

    @PatchMapping("/repair-parts/{id}")
    public ResponseEntity<RepairPartDTO> partialUpdateRepairPart(
            @PathVariable Long id,
            @RequestBody RepairPart repairPart
    ) {
        RepairPartDTO updatedRepairPartDTO = repairPartService.partialUpdate(id, repairPart);
        return new ResponseEntity<>(updatedRepairPartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/repair-parts/{id}")
    public ResponseEntity deleteRepairPart(@PathVariable Long id) {
        repairPartService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
