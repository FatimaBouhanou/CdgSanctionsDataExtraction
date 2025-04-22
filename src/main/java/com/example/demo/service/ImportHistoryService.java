package com.example.demo.service;

import com.example.demo.constants.FileType;
import com.example.demo.model.ImportHistory;
import com.example.demo.repository.ImportHistoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportHistoryService {

    private final ImportHistoryRepository importHistoryRepository;

    @Value("#{'${import.data.url}'.split(',')}")
    private List<String> importUrls;

    //importing data
    @PostConstruct
    public void processImportData() {
        for (String urlStr : importUrls) {
            try {
                Path downloadedFile = downloadFile(urlStr);
                long lineCount = countAllLines(downloadedFile);
                double sizeInMB = calculateFileSizeInMB(downloadedFile);

                String fileName = downloadedFile.getFileName().toString();
                FileType fileType = detectFileType(fileName);

                ImportHistory history = new ImportHistory(
                        fileName,
                        LocalDateTime.now(),
                        sizeInMB,
                        lineCount,
                        fileType
                );

                importHistoryRepository.save(history);

                // Cleanup if needed
                Files.deleteIfExists(downloadedFile);

            } catch (IOException e) {
                System.err.println("Error processing URL: " + urlStr);
                e.printStackTrace();
            }
        }
    }


    //downloading the file
    private Path downloadFile(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        String fileName = Paths.get(url.getPath()).getFileName().toString();
        Path targetPath = Files.createTempFile(fileName.replace(".csv", ""), ".csv");
        Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return targetPath;
    }


    //line counter
    private long countAllLines(Path filePath) {
        long lines = 0;
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            while (reader.readLine() != null) {
                lines++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }


    //calculating file size
    private double calculateFileSizeInMB(Path filePath) throws IOException {
        long bytes = Files.size(filePath);
        return Math.round((bytes / (1024.0 * 1024.0)) * 100.0) / 100.0;
    }


    //detecting and matching the file type to the file
    private FileType detectFileType(String fileName) {
        fileName = fileName.toLowerCase();
        if (fileName.contains("peps")) {
            return FileType.PEPS;
        } else if (fileName.contains("securities")) {
            return FileType.SANCTIONED_SECURITIES;
        } else if (fileName.contains("warrant") || fileName.contains("crime")) {
            return FileType.WARRANTS_AND_CRIMINALS;
        } else {
            throw new IllegalArgumentException("Unknown file type for: " + fileName);
        }
    }
}
