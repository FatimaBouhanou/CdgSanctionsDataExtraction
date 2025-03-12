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
    @Column(name = "last_name")
    private String name;

    @Column(name = "sanction_type")
    private String sdnType;

    @ElementCollection
    @CollectionTable(name = "sanction_programs", joinColumns = @JoinColumn(name = "sanction_id"))
    @Column(name = "program_name")
    private List<String> programs;

    @Column(name = "country")
    private String country;

    @Column(name = "sanction_list")
    private String sanctionList = "OFAC";

    @Column(name = "reason")
    private String reason = "Sanctioned by OFAC";



    public void setUid(String uid) {
        if (uid == null || uid.trim().isEmpty()) {
            throw new IllegalArgumentException("UID cannot be null or empty");
        }
        this.uid = uid;
    }
}
