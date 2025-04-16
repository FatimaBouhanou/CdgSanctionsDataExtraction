package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sanctions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanctionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // or AUTO/IDENTITY depending on your DB
    @Column(name = "uid", nullable = false, updatable = false)
    private UUID uid;


    @Column(name = "id")
    private String id;

    @Column(name = "data_schema")
    private String schema;

    @Column(nullable = false)
    private String name;

    @Column(name = "aliases")
    private String aliases;


    private String birth_date;

    @Column(name = "countries")
    private String countries;

    @Column(name = "addresses")
    private String addresses;

    @Column(name = "identifiers")
    private String identifiers;

    @Column(name = "sanctions")
    private String sanctions;

    @Column(name = "phones")
    private String phones;

    @Column(name = "emails")
    private String emails;

    private String dataset;


    private String first_seen;


    private String last_seen;


    private String last_change;


}
