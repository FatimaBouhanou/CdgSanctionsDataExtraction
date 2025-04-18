package com.example.demo.config;

import com.example.demo.model.SanctionedEntity;
import com.example.demo.repository.SanctionedEntityRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.UrlResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);

    private static final String HARDCODED_URL = "https://data.opensanctions.org/datasets/20250413/peps/targets.simple.csv";

    @Bean
    public FlatFileItemReader<SanctionedEntity> csvReader() throws MalformedURLException {
        UrlResource resource = new UrlResource(HARDCODED_URL);

        if (!resource.exists()) {
            throw new MalformedURLException("CSV file not found at: " + HARDCODED_URL);
        }

        FlatFileItemReader<SanctionedEntity> reader = new FlatFileItemReader<>();
        reader.setResource(resource);
        reader.setEncoding(StandardCharsets.UTF_8.name());
        reader.setLinesToSkip(1);

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setQuoteCharacter('"');
        tokenizer.setStrict(false);
        tokenizer.setNames(
                "id", "schema", "name", "aliases", "birth_date",
                "countries", "addresses", "identifiers", "sanctions",
                "phones", "emails", "dataset", "first_seen", "last_seen", "last_change"
        );

        BeanWrapperFieldSetMapper<SanctionedEntity> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(SanctionedEntity.class);

        DefaultLineMapper<SanctionedEntity> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(mapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public ItemProcessor<SanctionedEntity, SanctionedEntity> processor() {
        return entity -> {
            if (entity != null) {
                log.debug("Processing entity: {}", entity.getName());
                return entity;
            }
            return null;
        };
    }

    @Bean
    public ItemWriter<SanctionedEntity> writer(SanctionedEntityRepository repository) {
        return items -> {
            if (items != null && !items.isEmpty()) {
                log.info(">>> Saving {} entities...", items.size());

                try {
                    repository.saveAll(items);
                    log.info(">>> Batch saveAll completed.");
                } catch (Exception e) {
                    log.error(">>> Error saving batch: {}", e.getMessage(), e);
                    for (SanctionedEntity item : items) {
                        log.error("Failed entity: {}", item);
                    }
                }
            } else {
                log.warn(">>> Received empty item list for writing.");
            }
        };
    }


    @Bean
    public Step importCsvStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              ItemReader<SanctionedEntity> csvReader,
                              ItemWriter<SanctionedEntity> writer,
                              ItemProcessor<SanctionedEntity, SanctionedEntity> processor) {
        return new StepBuilder("importCsvStep", jobRepository)
                .<SanctionedEntity, SanctionedEntity>chunk(100, transactionManager)
                .reader(csvReader)
                .processor(processor)
                .writer(writer)
               // .allowStartIfComplete(true)  // allow step to re-execute
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .build();
    }

    @Bean
    public Job importSanctionsJob(JobRepository jobRepository,
                                  Step importCsvStep,
                                  JobExecutionListener listener) { // inject here
        return new JobBuilder("importSanctionsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener) // use the injected one
                .start(importCsvStep)
                .build();
    }


    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public JobExecutionListener listener() {
        return new JobExecutionListenerSupport() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.info("*** Batch job is starting...");
                System.out.println("=== DEBUG: Listener is working ===");
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                log.info("*** Batch job finished with status: {}", jobExecution.getStatus());
            }
        };
    }
}