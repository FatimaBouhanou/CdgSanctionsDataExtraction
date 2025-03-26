package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "sanctions")


public class SanctionedEntity {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(unique = true, nullable = false)
//    private String uid;

    @Column(name = "name", nullable = false)
    private String sanctionedName;

    @Column(name = "sdnType", nullable = false)
    private String sanctionType;

//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "sanction_programs", joinColumns = @JoinColumn(name = "sanction_id"))
//    @Column(name = "programs")
//    private List<String> programs;

    @Column(name = "country", nullable = false)
        private String sanctionCountry;

    @Column(name = "sanctionList", nullable = false)
    private String sanctionList = "OFAC";

    @Column(name = "reason", nullable = false)
    private String SanctionReason = "Sanctioned by OFAC";

    // Additional Constructor with Default Values
    public SanctionedEntity( String name, String sdnType, String country) {

        this.sanctionedName = name;
        this.sanctionType = sdnType;
        //this.programs = programs;
        this.sanctionCountry = country;
        this.sanctionList = "OFAC";  // Default Value
        this.SanctionReason = "Sanctioned by OFAC"; // Default Value
    }

//    public void setUid(String uid) {
//        if (uid == null || uid.trim().isEmpty()) {
//            throw new IllegalArgumentException("UID cannot be null or empty");
//        }
//        this.uid = uid;
//    }


    // Explicit No-Args Constructor
   public SanctionedEntity() {
       // Avoid null issues
    }

    public void setSanctionedName(String name) {
        this.sanctionedName = name;
    }

    public void setSanctionType(String sdnType) {
        this.sanctionType = sdnType;
    }

//    public void setPrograms(List<String> programs) {
//        this.programs = programs;
//    }

    public void setSanctionCountry(String country) {
        this.sanctionCountry = country;
    }

    public void setSanctionList(String sanctionList) {
        this.sanctionList = sanctionList;
    }

    public void setSanctionReason(String reason) {
        this.SanctionReason = reason;
    }

    public Long getId() {
        return id;
    }



    public String getSanctionedName() {
        return sanctionedName;
    }

    public String getSanctionType() {
        return sanctionType;
    }

//    public List<String> getPrograms() {
//        return programs;
//    }

    public String getSanctionCountry() {
        return sanctionCountry;
    }

    public String getSanctionList() {
        return sanctionList;
    }

    public String getSanctionReason() {
        return SanctionReason;
    }
}
