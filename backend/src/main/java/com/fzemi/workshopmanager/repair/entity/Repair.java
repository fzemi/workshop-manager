package com.fzemi.workshopmanager.repair.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.repair.config.RepairTypeFormat;
import com.fzemi.workshopmanager.repairparts.entity.RepairPart;
import com.fzemi.workshopmanager.vehicle.entity.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "repairs")
public class Repair {
    // TODO: Add the missing foreign keys from not implemented tables

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Repair number ex. 21G-01-2024
     */
    @Column(unique = true)
    private String number;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date expectedEndDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RepairTypeFormat type;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @JsonIgnore
    public List<Client> getClients() {
        return vehicle.getClients();
    }
}
