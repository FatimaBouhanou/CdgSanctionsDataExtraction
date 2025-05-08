package com.example.demo.service;

import com.example.demo.constants.FileType;
import com.example.demo.model.ImportHistory;
import com.example.demo.repository.ImportHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportHistoryService {

    private final ImportHistoryRepository importHistoryRepository;

    //data url
    @Value("#{'${import.data.url}'.split(',')}")
    private List<String> importUrls;

    //folder path
    @Value("${import.data.folder}")
    private String importFolder;


    //logging data
    @Transactional
    public void logAllImports() {
        for (String fileUrl : importUrls) {
            try {
                String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
                LocalDateTime importDate = LocalDateTime.now();
                FileType fileType = detectFileType(fileUrl);

                // Download to temp file for processing
                Path downloadedFile = downloadFile(fileUrl);

                // Save the same file to the configured folder
                downloadFileToDirectory(fileUrl);

                double fileSizeInMB = calculateFileSizeInMB(downloadedFile);
                long linesCount = countCsvRecords(downloadedFile);

                log.info(">>> [{}] File metadata:", fileName);
                log.info("- Size: {} MB", fileSizeInMB);
                log.info("- Lines: {}", linesCount);
                log.info("- File Type: {}", fileType);

                // Delete old duplicate if it exists
                List<ImportHistory> duplicates = importHistoryRepository
                        .findByFileNameAndFileTypeAndFileSizeAndNumberOfLines(
                                fileName, fileType, fileSizeInMB, linesCount
                        );

                if (!duplicates.isEmpty()) {
                    importHistoryRepository.deleteAll(duplicates);
                    log.info(">>> Deleted {} existing duplicate(s) for: {} [{}]", duplicates.size(), fileName, fileType);
                }

                // Save new record
                ImportHistory newHistory = new ImportHistory(fileName, importDate, fileSizeInMB, linesCount, fileType);
                ImportHistory saved = importHistoryRepository.save(newHistory);
                log.info(">>> Saved new record with ID: {}", saved.getId());

            } catch (Exception e) {
                log.error("Failed to process URL {}: {}", fileUrl, e.getMessage(), e);
            }
        }
    }



    //counting lines
    private Path downloadFile(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        String name = Paths.get(url.getPath()).getFileName().toString();
        Path temp = Files.createTempFile(name.replace(".csv", ""), ".csv");
        Files.copy(url.openStream(), temp, StandardCopyOption.REPLACE_EXISTING);
        return temp;
    }

    private long countCsvRecords(Path filePath) throws IOException {
        long count = 0;
        try (BufferedReader reader = Files.newBufferedReader(filePath, UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreSurroundingSpaces().withTrim())) {

            for (CSVRecord record : csvParser) {
                if (!recordIsEmpty(record)) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean recordIsEmpty(CSVRecord record) {
        for (String value : record) {
            if (value != null && !value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }


    //calculating size
    private double calculateFileSizeInMB(Path filePath) throws IOException {
        long bytes = Files.size(filePath);
        return Math.round((bytes / (1024.0 * 1024.0)) * 100.0) / 100.0;
    }


    //detecting file type
    private FileType detectFileType(String fileName) {
        String fn = fileName.toLowerCase();
        if (fn.contains("peps")) {
            return FileType.PEPS;
        } else if (fn.contains("securities")) {
            return FileType.SANCTIONED_SECURITIES;
        } else if (fn.contains("warrant") || fn.contains("crime")) {
            return FileType.WARRANTS_AND_CRIMINALS;
        } else {
            return FileType.UNKNOWN;
        }
    }

    public List<ImportHistory> getAllImportHistory() {
        return importHistoryRepository.findAll();
    }


    //downloading files to data directory
    private void downloadFileToDirectory(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        String originalFileName = Paths.get(url.getPath()).getFileName().toString(); // Get original filename
        FileType fileType = detectFileType(urlStr);
        String typeName = fileType.name().toLowerCase().replace("_", "-"); // Convert file type to string (e.g., "peps", "warrants-and-criminals")

        // Construct the file name using the file type to ensure uniqueness
        String uniqueFileName = typeName + "_" + originalFileName;

        // Construct the full file path
        Path targetDir = Paths.get(importFolder);
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        Path targetPath = targetDir.resolve(uniqueFileName);

        // Delete the existing file if it exists (replace with new one)
        if (Files.exists(targetPath)) {
            Files.delete(targetPath);
            log.info(">>> Existing file {} deleted.", targetPath);
        }

        // Download the file
        Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        log.info("<<< File saved to local folder as: {}", targetPath);
    }



}
