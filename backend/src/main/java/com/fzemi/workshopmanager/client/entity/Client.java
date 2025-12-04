package com.fzemi.workshopmanager.client.entity;

import com.fzemi.workshopmanager.repair.entity.Repair;
import com.fzemi.workshopmanager.vehicle.entity.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"vehicles", "repairs"})
@Builder
@Entity
@Table(name = "clients")
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

    @ManyToMany(mappedBy = "clients")
    @Builder.Default
    private List<Vehicle> vehicles = new ArrayList<>();

    @ManyToMany(mappedBy = "clients")
    @Builder.Default
    private List<Repair> repairs = new ArrayList<>();
}
