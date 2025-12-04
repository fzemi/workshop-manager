package com.fzemi.workshopmanager.vehicle.entity;

import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.vehicle.config.FuelFormat;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "clients")
@Builder
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vin;

    private String manufacturer;

    private String model;

    private String licencePlate;

    @Temporal(TemporalType.DATE)
    private Date productionDate;

    private String color;

    private Float engineCapacity;

    @Column(nullable = false)
    private FuelFormat fuelType;

    /**
     * Power in kW
     */
    private Integer power;

    @ManyToMany
    @JoinTable(name = "vehicle_owners")
    @Builder.Default
    private List<Client> clients = new ArrayList<>();
}
