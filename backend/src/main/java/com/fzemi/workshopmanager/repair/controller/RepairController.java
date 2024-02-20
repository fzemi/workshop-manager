package com.fzemi.workshopmanager.repair.controller;

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
    public ResponseEntity<List<Repair>> listRepairs() {
        return ResponseEntity.ok(repairService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Repair> getRepairById(@PathVariable Long id) {
        Optional<Repair> foundRepair = repairService.findRepairById(id);
        return foundRepair.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<Repair> getRepairByNumber(@PathVariable String number) {
        Optional<Repair> foundRepair = repairService.findRepairByNumber(number);
        return foundRepair.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Repair> createRepair(@RequestBody Repair repair) {
        Repair createdRepair = repairService.save(repair);
        return new ResponseEntity<>(createdRepair, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Repair> fullUpdateRepair(
            @PathVariable Long id,
            @RequestBody Repair repair) {
        Optional<Repair> foundRepair = repairService.findRepairById(id);

        if (foundRepair.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        repair.setId(id);
        Repair updatedRepair = repairService.save(repair);
        return new ResponseEntity<>(updatedRepair, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Repair> partialUpdateRepair(
            @PathVariable Long id,
            @RequestBody Repair repair) {
        Optional<Repair> foundRepair = repairService.findRepairById(id);

        if (foundRepair.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Repair updatedRepair = repairService.partialUpdate(id, repair);
        return new ResponseEntity<>(updatedRepair, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteRepair(@PathVariable Long id) {
        Optional<Repair> foundRepair = repairService.findRepairById(id);

        if (foundRepair.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        repairService.delete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
