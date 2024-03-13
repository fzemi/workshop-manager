package com.fzemi.workshopmanager.vehicle.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.vehicle.config.FuelFormat;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vehicles")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
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
    private List<Client> clients;
}
