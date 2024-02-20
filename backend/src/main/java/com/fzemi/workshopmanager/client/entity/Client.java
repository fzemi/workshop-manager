package com.fzemi.workshopmanager.client.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;

    private String surname;

    @Column(unique = true)
    private String pesel;

    @Column(unique = true)
    private String nip;

    private String phoneNumber;

    @Column(unique = true)
    private String email;

    private String country;

    private String postalCode;

    private String city;

    private String address;

    @Temporal(TemporalType.DATE)
    private Date birthDate;
}
