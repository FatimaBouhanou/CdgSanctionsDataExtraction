package com.example.demo.repository;

import com.example.demo.constants.FileType;
import com.example.demo.model.ImportHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImportHistoryRepository extends JpaRepository<ImportHistory, Long> {

    List<ImportHistory> findByFileNameAndFileTypeAndFileSizeAndNumberOfLines(String fileName, FileType fileType, double fileSizeInMB, long linesCount);
}
