package com.example.demo.service;

import com.example.demo.extractors.DataExtractor;
import com.example.demo.factory.DataExtractorFactory;
import com.example.demo.model.SanctionedEntity;
import com.example.demo.model.DataSourceEntity;
import com.example.demo.repository.SanctionedEntityRepository;
import com.example.demo.repository.DataSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataIngestionService {

    private final DataExtractorFactory extractorFactory;
    private final SanctionedEntityRepository repository;
    private final DataSourceRepository dataSourceRepository;

    @Autowired
    public DataIngestionService(DataExtractorFactory extractorFactory,
                                SanctionedEntityRepository repository,
                                DataSourceRepository dataSourceRepository) {
        this.extractorFactory = extractorFactory;
        this.repository = repository;
        this.dataSourceRepository = dataSourceRepository;
    }

    /**
     * Ingests data from all enabled sources.
     */
    public void ingestAllData() {
        List<DataSourceEntity> sources = dataSourceRepository.findByEnabledTrue(); // ✅ Fetch only enabled sources

        if (sources.isEmpty()) {
            System.err.println("No enabled data sources found.");
            return;
        }

        for (DataSourceEntity source : sources) {
            ingestDataFromSource(source.getSourceUrl());
        }
    }

    /**
     * Ingests data from a single source URL.
     * @param sourceUrl The URL of the data source.
     */
    public void ingestDataFromSource(String sourceUrl) {
        try {
            System.out.println("Starting data extraction from: " + sourceUrl);

            DataExtractor extractor = extractorFactory.getExtractor(sourceUrl);
            List<SanctionedEntity> entities = extractor.extractData(sourceUrl);

            if (entities.isEmpty()) {
                System.err.println("No data extracted from: " + sourceUrl);
                return;
            }

            repository.saveAll(entities);
            System.out.println("Data ingestion successful for: " + sourceUrl);
        } catch (Exception e) {
            System.err.println("Error processing source: " + sourceUrl + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

}
