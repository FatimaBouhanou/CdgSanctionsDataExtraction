package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sanctions")
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)  // Ignore unknown properties
public class SanctionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JacksonXmlProperty(localName = "lastName")  // Correctly maps to <lastName>
    private String name;

    @JacksonXmlProperty(localName = "country")  // Correctly maps to <country>
    private String country;

    private String sanctionList = "OFAC";  // Hardcoded value

    private String reason = "Sanctioned by OFAC";  // Default reason
}
