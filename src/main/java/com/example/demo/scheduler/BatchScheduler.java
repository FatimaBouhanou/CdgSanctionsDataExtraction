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

    private final JobLauncher jobLauncher;
    private final Job importSanctionsJob;

    public BatchScheduler(JobLauncher jobLauncher, Job importSanctionsJob) {
        this.jobLauncher = jobLauncher;
        this.importSanctionsJob = importSanctionsJob;
    }

    @Scheduled(fixedRate = 60000) // or cron, etc.
    public void runBatchJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) //  unique param
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(importSanctionsJob, jobParameters);
            System.out.println("Job execution status: " + jobExecution.getStatus());
        } catch (Exception e) {
            System.err.println("Job failed to start");
            e.printStackTrace();
        }
    }
}
