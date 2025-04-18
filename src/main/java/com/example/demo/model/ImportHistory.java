package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table
@Data
@NoArgsConstructor

public class ImportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String fileName;
private LocalDateTime importDate;
private long fileSize;
private long numberOfLines;

    public ImportHistory(String s, LocalDateTime now, long size, long lines) {
        this.fileName=s;
        this.importDate=now;
        this.fileSize=size;
        this.numberOfLines=lines;
    }
}
