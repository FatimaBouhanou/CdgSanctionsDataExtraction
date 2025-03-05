package com.example.demo.service;

import com.example.demo.extractors.DataExtractor;
import com.example.demo.factory.DataExtractorFactory;
import com.example.demo.model.SanctionedEntity;
import com.example.demo.repository.SanctionedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DataIngestionService {
    private final DataExtractorFactory extractorFactory;
    private final SanctionedEntityRepository repository;

    @Autowired
    public DataIngestionService(DataExtractorFactory extractorFactory, SanctionedEntityRepository repository) {
        this.extractorFactory = extractorFactory;
        this.repository = repository;
    }

    public void ingestData(String source) {
        try {
            // Start of data extraction
            System.out.println("Starting data extraction from source: " + source);

            DataExtractor extractor = extractorFactory.getExtractor(source);
            List<SanctionedEntity> entities = extractor.extracData(source);

            // After extraction
            System.out.println("Data extraction complete. Extracted " + (entities != null ? entities.size() : 0) + " entities.");

            if (entities != null && !entities.isEmpty()) {
                // Before saving data
                System.out.println("Saving " + entities.size() + " entities to the database...");

                repository.saveAll(entities);

                // After saving
                System.out.println("Data ingestion successful. Saved " + entities.size() + " entities.");
            } else {
                System.out.println("No data extracted or the list is empty.");
            }
        } catch (IOException e) {
            System.err.println("Error during data ingestion: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
