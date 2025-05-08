package com.example.demo.scheduler;

import com.example.demo.service.ImportHistoryService; // Import the ImportHistoryService
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataIngestionScheduler {

    private final ImportHistoryService importHistoryService;  // Inject ImportHistoryService

    @Autowired
    public DataIngestionScheduler(ImportHistoryService importHistoryService) { // Inject ImportHistoryService here
        this.importHistoryService = importHistoryService; // Initialize ImportHistoryService
    }

    @Scheduled(cron = "*/30 * * * * *")  //

    public void logImportHistory() {
        try {
            // Log the import history
            importHistoryService.logAllImports();  // Trigger the logging of import history
            System.out.println("Import history logged successfully.");
        } catch (Exception e) {
            System.err.println("Error logging import history: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
