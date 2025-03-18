package com.example.demo.scheduler;

import com.example.demo.extractors.XmlDataExtractor;
import com.example.demo.extractors.CsvDataExtractor;
import com.example.demo.model.SanctionedEntity;
import com.example.demo.model.DataSourceEntity;
import com.example.demo.model.DataType;
import com.example.demo.repository.SanctionedEntityRepository;
import com.example.demo.repository.DataSourceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataIngestionScheduler {

    private final XmlDataExtractor xmlDataExtractor;
    private final CsvDataExtractor csvDataExtractor;
    private final SanctionedEntityRepository sanctionedEntityRepository;
    private final DataSourceRepository dataSourceRepository;

    @Autowired
    public DataIngestionScheduler(XmlDataExtractor xmlDataExtractor,
                                  CsvDataExtractor csvDataExtractor,
                                  SanctionedEntityRepository sanctionedEntityRepository,
                                  DataSourceRepository dataSourceRepository) {
        this.xmlDataExtractor = xmlDataExtractor;
        this.csvDataExtractor = csvDataExtractor;
        this.sanctionedEntityRepository = sanctionedEntityRepository;
        this.dataSourceRepository = dataSourceRepository;
    }

    @Scheduled(cron = "*/30 * * * * *")  // Runs every 30 seconds
    @Transactional
    public void fetchAndStoreData() {
        List<DataSourceEntity> sources = dataSourceRepository.findByEnabledTrue();

        if (sources.isEmpty()) {
            System.err.println("No sanctioned data sources found in the database.");
            return;
        }

        for (DataSourceEntity source : sources) {
            try {
                System.out.println("Fetching data from: " + source.getSourceUrl());
                List<SanctionedEntity> entities;

                if (source.getDataType() == DataType.XML) {
                    entities = xmlDataExtractor.extractData(source.getSourceUrl());
                } else if (source.getDataType() == DataType.CSV) {
                    entities = csvDataExtractor.extractData(source.getSourceUrl());
                } else {
                    System.err.println("Unsupported data type: " + source.getDataType());
                    continue;
                }

                for (SanctionedEntity entity : entities) {
                    if (!sanctionedEntityRepository.existsByName(entity.getName())) {
                        sanctionedEntityRepository.save(entity);
                        System.out.println("Saved: " + entity.getName());
                    }
                }
                System.out.println("Sanctions list updated successfully for: " + source.getSourceUrl());
            } catch (Exception e) {
                System.err.println("Error processing source: " + source.getSourceUrl());
                e.printStackTrace();
            }
        }
    }
}
