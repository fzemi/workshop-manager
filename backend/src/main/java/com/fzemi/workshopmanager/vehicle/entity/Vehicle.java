package com.fzemi.workshopmanager.vehicle.entity;

import com.fzemi.workshopmanager.vehicle.config.FuelFormat;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
