package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "sanctions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanctionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uid;

    @Column(name = "last_name", nullable = false)
    private String name;

    @Column(name = "sanction_type", nullable = false)
    private String sdnType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "sanction_programs", joinColumns = @JoinColumn(name = "sanction_id"))
    @Column(name = "program_name")
    private List<String> programs;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "sanction_list", nullable = false)
    private String sanctionList = "OFAC";

    @Column(name = "reason", nullable = false)
    private String reason = "Sanctioned by OFAC";

    // Additional Constructor with Default Values
    public SanctionedEntity(String uid, String name, String sdnType, List<String> programs, String country) {
        this.uid = uid;
        this.name = name;
        this.sdnType = sdnType;
        this.programs = programs;
        this.country = country;
        this.sanctionList = "OFAC";  // Default Value
        this.reason = "Sanctioned by OFAC"; // Default Value
    }

    public void setUid(String uid) {
        if (uid == null || uid.trim().isEmpty()) {
            throw new IllegalArgumentException("UID cannot be null or empty");
        }
        this.uid = uid;
    }
}
