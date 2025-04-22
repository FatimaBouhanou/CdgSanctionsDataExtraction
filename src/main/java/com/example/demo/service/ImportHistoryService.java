package com.example.demo.service;

import com.example.demo.model.ImportHistory;
import com.example.demo.repository.ImportHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportHistoryService {

    private final ImportHistoryRepository historyRepository;

    @Value("${import.data.url}")
    private String fileUrl;

    @Transactional
    public void logImportMetadata() {
        try {
            URL url = new URL(fileUrl);
            URLConnection connection = url.openConnection();

            long fileSizeInBytes = connection.getContentLengthLong();
            double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024); // Convert to MB

            long linesCount;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                linesCount = reader.lines().skip(1).count(); // Skip header
            }

            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            LocalDateTime importDate = LocalDateTime.now();

            log.info(">>> Metadata:");
            log.info(" - File Name: {}", fileName);
            log.info(" - File Size: {} MB", String.format("%.2f", fileSizeInMB));
            log.info(" - Lines: {}", linesCount);
            log.info(" - Import Date: {}", importDate);

            ImportHistory history = new ImportHistory(fileName, importDate, fileSizeInMB, linesCount);
            ImportHistory saved = historyRepository.save(history);

            log.info(">>> ImportHistory saved: {}", saved);
            log.info(">>> Saved to DB with ID: {}", saved.getId());

        } catch (Exception e) {
            log.error(">>> Failed to save import metadata: {}", e.getMessage(), e);
        }
    }
}
