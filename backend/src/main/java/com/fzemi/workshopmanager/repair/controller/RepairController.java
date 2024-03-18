package com.fzemi.workshopmanager.repair.controller;

import com.fzemi.workshopmanager.repair.dto.RepairDTO;
import com.fzemi.workshopmanager.repair.dto.RepairWithClientsDTO;
import com.fzemi.workshopmanager.repair.entity.Repair;
import com.fzemi.workshopmanager.repair.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/repairs")
public class RepairController {
    private final RepairService repairService;

    @Autowired
    public RepairController(RepairService repairService) {
        this.repairService = repairService;
    }

    @GetMapping
    public ResponseEntity<List<RepairDTO>> listRepairs() {
        return ResponseEntity.ok(repairService.findAll());
    }

    @GetMapping("/withClients")
    public ResponseEntity<List<RepairWithClientsDTO>> listRepairsWithClients() {
        return ResponseEntity.ok(repairService.findAllWithClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepairDTO> getRepairById(@PathVariable Long id) {
        Optional<RepairDTO> foundRepairDTO = repairService.findRepairById(id);
        return foundRepairDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<RepairDTO> getRepairByNumber(@PathVariable String number) {
        Optional<RepairDTO> foundRepairDTO = repairService.findRepairByNumber(number);
        return foundRepairDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RepairDTO> createRepair(@RequestBody Repair repair) {
        RepairDTO createdRepairDTO = repairService.save(repair);
        return new ResponseEntity<>(createdRepairDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepairDTO> fullUpdateRepair(
            @PathVariable Long id,
            @RequestBody Repair repair) {
        Optional<RepairDTO> foundRepairDTO = repairService.findRepairById(id);

        if (foundRepairDTO.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        repair.setId(id);
        RepairDTO updatedRepairDTO = repairService.save(repair);
        return new ResponseEntity<>(updatedRepairDTO, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RepairDTO> partialUpdateRepair(
            @PathVariable Long id,
            @RequestBody Repair repair) {
        Optional<RepairDTO> foundRepairDTO = repairService.findRepairById(id);

        if (foundRepairDTO.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        RepairDTO updatedRepairDTO = repairService.partialUpdate(id, repair);
        return new ResponseEntity<>(updatedRepairDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteRepair(@PathVariable Long id) {
        Optional<RepairDTO> foundRepairDTO = repairService.findRepairById(id);

        if (foundRepairDTO.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        repairService.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
