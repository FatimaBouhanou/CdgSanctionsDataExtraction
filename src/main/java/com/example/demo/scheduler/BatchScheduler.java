package com.example.demo.scheduler;

import com.example.demo.extractors.CsvDataExtractor;
import com.example.demo.model.SanctionedEntity;
import com.example.demo.repository.SanctionedEntityRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
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

    @Scheduled(cron = "*/10 * * * * *") //

    public void runBatchJob() {
        try {
            logger.info("Attempting to launch job...");
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // paramètre unique
                    .toJobParameters();
            JobExecution execution = jobLauncher.run(importSanctionsJob, new JobParameters());
            logger.info("Job execution status: {}", execution.getStatus());
        } catch (Exception e) {
            logger.error("Job failed to start", e);
        }
    }
}
