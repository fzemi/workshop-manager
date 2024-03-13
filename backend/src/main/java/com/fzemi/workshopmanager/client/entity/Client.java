package com.fzemi.workshopmanager.client.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fzemi.workshopmanager.vehicle.entity.Vehicle;
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
@Table(name = "clients")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Client {
    // TODO: Find a way to make pesel, nip and email unique and nullable at the same time

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;

    private String surname;

    private String pesel;

    private String nip;

    private String phoneNumber;

    private String email;

    private String country;

    private String postalCode;

    private String city;

    private String address;

    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @ManyToMany
    private List<Vehicle> vehicles;
}
