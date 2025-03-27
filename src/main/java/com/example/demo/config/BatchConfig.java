package com.example.demo.config;

import com.example.demo.model.SanctionedEntity;
import com.example.demo.repository.SanctionedEntityRepository;
import com.example.demo.model.DataSourceEntity;
import com.example.demo.model.DataType;
import com.example.demo.repository.DataSourceRepository;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final DataSourceRepository sourceRepository;

    public BatchConfig(DataSourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    private String getFilePath(DataType type) {
        return sourceRepository.findFirstByDataTypeAndEnabledTrueOrderByIdDesc(type)
                .map(DataSourceEntity::getSourceUrl)
                .orElseThrow(() -> new RuntimeException(type + " file path not found in database"));
    }

    // CSV Reader with dynamic file path retrieval
    @Bean
    public FlatFileItemReader<SanctionedEntity> csvReader() {
        FlatFileItemReader<SanctionedEntity> reader = new FlatFileItemReader<>();
        String filePath = getFilePath(DataType.CSV);
        System.out.println("Reading CSV file from path: " + filePath); // Debugging line

        reader.setResource(new FileSystemResource(filePath));
        reader.setLinesToSkip(1);

        DefaultLineMapper<SanctionedEntity> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("sanctionCountry", "sanctionedName", "sanctionReason", "sanctionList", "sanctionType");
        tokenizer.setStrict(false);
        tokenizer.setQuoteCharacter('"');

        BeanWrapperFieldSetMapper<SanctionedEntity> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(SanctionedEntity.class);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }


    // Processor to handle deduplication
    @Bean
    public ItemProcessor<SanctionedEntity, SanctionedEntity> processor() {
        return entity -> {
            if (entity != null) {
                System.out.println("Processing: " + entity.getSanctionedName());
                return entity;
            }
            return null;  // Returning null would cause the record to be skipped
        };
    }




    // Writer
    @Bean
    public ItemWriter<SanctionedEntity> writer(SanctionedEntityRepository repository) {
        return items -> {
            for (SanctionedEntity item : items) {
                try {
                    System.out.println("Attempting to save: " + item.getSanctionedName());
                    repository.save(item);
                    System.out.println("Successfully saved: " + item.getSanctionedName());
                } catch (Exception e) {
                    System.err.println("Failed to save: " + item.getSanctionedName() + " due to: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }


    // CSV Step
    @Bean
    public Step importCsvStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                              ItemReader<SanctionedEntity> csvReader,
                              ItemWriter<SanctionedEntity> writer,
                              ItemProcessor<SanctionedEntity, SanctionedEntity> processor) {
        return new StepBuilder("importCsvStep", jobRepository)
                .<SanctionedEntity, SanctionedEntity>chunk(10, transactionManager) // Testing with a chunk size of 1
                .reader(csvReader)
                .processor(processor)
                .writer(writer)
                .build();
    }


    // Batch Job (without XML step)
    @Bean
    public Job importSanctionsJob(JobRepository jobRepository, Step importCsvStep) {
        System.out.println("Starting the importSanctionsJob");
        return new JobBuilder("importSanctionsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(importCsvStep)
                .build();
    }


    // Transaction Manager
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }




    //listener to monitor the jobs execution
    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new JobExecutionListenerSupport() {
            @Override
            public void afterJob(JobExecution jobExecution) {
                System.out.println("Job Status: " + jobExecution.getStatus());
                if (jobExecution.getStatus() == BatchStatus.FAILED) {
                    System.out.println("Failure details: " + jobExecution.getExitStatus());
                }
            }
        };
    }

    /*
    // XML Reader (Commented out)
    @Bean
    public StaxEventItemReader<SanctionedEntity> xmlReader() {
        // XML processing is disabled
        return null;
    }

    // XML Step (Commented out)
    @Bean
    public Step importXmlStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                              ItemReader<SanctionedEntity> xmlReader,
                              ItemWriter<SanctionedEntity> writer,
                              ItemProcessor<SanctionedEntity, SanctionedEntity> processor) {
        return new StepBuilder("importXmlStep", jobRepository)
                .<SanctionedEntity, SanctionedEntity>chunk(100, transactionManager)
                .reader(xmlReader)
                .processor(processor)
                .writer(writer)
                .build();
    }
    */
}