package com.example.demo.scheduler;

import com.example.demo.extractors.XmlDataExtractor;
import com.example.demo.extractors.CsvDataExtractor;
import com.example.demo.model.SanctionedEntity;
import com.example.demo.model.DataSourceEntity;
import com.example.demo.model.DataType;
import com.example.demo.repository.SanctionedEntityRepository;
import com.example.demo.repository.DataSourceRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class BatchScheduler {

    private static final Logger logger = LoggerFactory.getLogger(BatchScheduler.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importSanctionsJob;

    @Autowired
    private XmlDataExtractor xmlDataExtractor;

    @Autowired
    private CsvDataExtractor csvDataExtractor;

    @Autowired
    private SanctionedEntityRepository sanctionedEntityRepository;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Scheduled(cron = "0 * * * * *") // Runs every hour
    public void runBatchJob() {
        logger.info("Starting batch job...");

        try {
            List<DataSourceEntity> sources = dataSourceRepository.findByEnabledTrue();

            if (sources.isEmpty()) {
                logger.warn("No enabled data sources found. Skipping batch job.");
                return;
            }

            boolean dataInserted = false;

            for (DataSourceEntity source : sources) {
                try {
                    logger.info("Fetching data from: {}", source.getSourceUrl());
                    List<SanctionedEntity> entities = null;

                    if (source.getDataType() == DataType.XML) {
                        entities = xmlDataExtractor.extractData(source.getSourceUrl());
                    } else if (source.getDataType() == DataType.CSV) {
                        entities = csvDataExtractor.extractData(source.getSourceUrl());
                    } else {
                        logger.error("Unsupported data type: {}", source.getDataType());
                        continue;
                    }

                    if (entities == null || entities.isEmpty()) {
                        logger.warn("No data extracted from: {}", source.getSourceUrl());
                        continue;
                    }

                    for (SanctionedEntity entity : entities) {
                        if (!sanctionedEntityRepository.existsBySanctionedName(entity.getSanctionedName())) {
                            sanctionedEntityRepository.save(entity);
                            logger.info("Saved new entity: {}", entity.getSanctionedName());
                            dataInserted = true;
                        } else {
                            logger.debug("Entity already exists: {}", entity.getSanctionedName());
                        }
                    }

                    logger.info("Sanctions list updated successfully for: {}", source.getSourceUrl());
                } catch (Exception e) {
                    logger.error("Error processing source: {} - {}", source.getSourceUrl(), e.getMessage(), e);
                }
            }

            if (dataInserted) {
                logger.info("Running Spring Batch job after data ingestion...");
                JobParameters jobParameters = new JobParametersBuilder()
                        .addLong("startAt", System.currentTimeMillis())
                        .toJobParameters();
                jobLauncher.run(importSanctionsJob, jobParameters);
                logger.info("Batch job executed successfully.");
            } else {
                logger.info("No new data inserted. Skipping batch job execution.");
            }

        } catch (Exception e) {
            logger.error("Error running batch job: {}", e.getMessage(), e);
        }
    }
}
