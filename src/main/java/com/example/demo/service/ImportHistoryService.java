package com.example.demo.service;

import com.example.demo.constants.FileType;
import com.example.demo.model.ImportHistory;
import com.example.demo.repository.ImportHistoryRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportHistoryService {

    private final ImportHistoryRepository importHistoryRepository;

    @Value("#{'${import.data.url}'.split(',')}")
    private List<String> importUrls;

    @Transactional
    public void logAllImports() {
        for (String fileUrl : importUrls) {
            try {
                URL url = new URL(fileUrl.trim());
                URLConnection connection = url.openConnection();

                long fileSizeInBytes = connection.getContentLengthLong();
                double fileSizeInMB = Math.round((fileSizeInBytes / (1024.0 * 1024)) * 100.0) / 100.0;

                long linesCount;
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    linesCount = reader.lines().skip(1).count();
                }

                String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
                LocalDateTime importDate = LocalDateTime.now();
                FileType fileType = detectFileType(fileUrl);

                log.info(">>> [{}] File metadata:", fileName);
                log.info(" - Size: {} MB", fileSizeInMB);
                log.info(" - Lines: {}", linesCount);
                log.info(" - File Type: {}", fileType);

                ImportHistory history = new ImportHistory(fileName, importDate, fileSizeInMB, linesCount, fileType);
                ImportHistory saved = importHistoryRepository.save(history);

                log.info(">>> Saved with ID: {}", saved.getId());

            } catch (Exception e) {
                log.error("Failed to process URL {}: {}", fileUrl, e.getMessage(), e);
            }
        }
    }


    private Path downloadFile(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        String name = Paths.get(url.getPath()).getFileName().toString();
        Path temp = Files.createTempFile(name.replace(".csv", ""), ".csv");
        Files.copy(url.openStream(), temp, StandardCopyOption.REPLACE_EXISTING);
        return temp;
    }

    private long countAllLines(Path filePath) throws IOException {
        try (BufferedReader r = Files.newBufferedReader(filePath, UTF_8)) {
            return r.lines().count();
        }
    }

    private double calculateFileSizeInMB(Path filePath) throws IOException {
        long bytes = Files.size(filePath);
        return Math.round((bytes / (1024.0 * 1024.0)) * 100.0) / 100.0;
    }

    private FileType detectFileType(String fileName) {
        String fn = fileName.toLowerCase();
        if (fn.contains("peps")) {
            return FileType.PEPS;
        } else if (fn.contains("securities")) {
            return FileType.SANCTIONED_SECURITIES;
        } else if (fn.contains("warrants") || fn.contains("criminals")) {
            return FileType.WARRANTS_AND_CRIMINALS;
        }
        throw new IllegalArgumentException("Unknown file type for: " + fileName);
    }
}

