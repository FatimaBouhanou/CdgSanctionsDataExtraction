package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "data_source_entity")
public class DataSourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sourceName;

    @Column(nullable = false, unique = true)
    private String sourceUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DataType dataType;

    @Column(nullable = false)
    private boolean enabled = true;


    public void setId(Long id) {
        this.id = id;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public DataType getDataType() {
        return dataType;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
