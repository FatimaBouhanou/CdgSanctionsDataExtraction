package com.example.demo.service;

import com.example.demo.model.ImportHistory;
import com.example.demo.repository.ImportHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportHistoryService {

    private final ImportHistoryRepository historyRepository;

    @Transactional
    @Async
    public CompletableFuture<Void> logImportMetadata(String fileUrl) {
        try {
            // Timeout setup to avoid long waiting times
            URL url = new URL(fileUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);  // Set connection timeout (5 seconds)
            connection.setReadTimeout(10000);    // Set read timeout (10 seconds)

            Long fileSize = connection.getContentLengthLong();
            long linesCount;

            // Try to open and read the file (skip header and count lines)
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                linesCount = reader.lines().skip(1).count(); // Skip header
            }

            // Extract file name from the URL
            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            LocalDateTime importDate = LocalDateTime.now();

            log.info(">>> Metadata:");
            log.info(" - File Name: {}", fileName);
            log.info(" - File Size: {} bytes", fileSize);
            log.info(" - Lines: {}", linesCount);
            log.info(" - Import Date: {}", importDate);

            // Save metadata to the database
            ImportHistory history = new ImportHistory(fileName, importDate, fileSize, linesCount);
            ImportHistory saved = historyRepository.save(history);

            // Log confirmation and saved data
            log.info(">>> ImportHistory saved: {}", saved);
            log.info(">>> Saved to DB with ID: {}", saved.getId());

        } catch (Exception e) {
            log.error(">>> Failed to save import metadata: {}", e.getMessage(), e);
        }
        return CompletableFuture.completedFuture(null);
    }
}
