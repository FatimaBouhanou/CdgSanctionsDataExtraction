package com.example.demo.model;

import com.example.demo.constants.FileType;
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
private double fileSize;
private long numberOfLines;
    @Enumerated(EnumType.STRING)
    private FileType fileType;



    public ImportHistory(String s, LocalDateTime now, double size, long lines, FileType fileType) {
        this.fileName=s;
        this.importDate=now;
        this.fileSize=size;
        this.numberOfLines=lines;
        this.fileType=fileType;
    }
}
