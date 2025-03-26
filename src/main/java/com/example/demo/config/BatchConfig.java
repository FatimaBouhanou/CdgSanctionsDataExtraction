package com.example.demo.config;

import com.example.demo.model.SanctionedEntity;
import com.example.demo.repository.SanctionedEntityRepository;
import com.example.demo.model.DataSourceEntity;
import com.example.demo.model.DataType;
import com.example.demo.repository.DataSourceRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

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

    // XML Reader with improved URL handling
    @Bean
    public StaxEventItemReader<SanctionedEntity> xmlReader() {
        StaxEventItemReader<SanctionedEntity> reader = new StaxEventItemReader<>();
        String filePath = getFilePath(DataType.XML);

        // Check if the filePath is a valid URL or local file path
        try {
            if (filePath.startsWith("http") || filePath.startsWith("https")) {
                reader.setResource(new UrlResource(filePath));
            } else {
                reader.setResource(new FileSystemResource(filePath));  // Handle local file path
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid file URL: " + filePath, e);
        }

        reader.setFragmentRootElementName("Sanction");

        XStreamMarshaller unmarshaller = new XStreamMarshaller();
        Map<String, Class<SanctionedEntity>> aliases = new HashMap<>();
        aliases.put("Sanction", SanctionedEntity.class);
        unmarshaller.setAliases(aliases);
        reader.setUnmarshaller(unmarshaller);

        return reader;
    }


    // Processor to handle deduplication
    @Bean
    public ItemProcessor<SanctionedEntity, SanctionedEntity> processor(SanctionedEntityRepository repository) {
        return entity -> {
            if (entity.getSanctionCountry() == null || entity.getSanctionCountry().trim().isEmpty()) {
                entity.setSanctionCountry("Unknown");
            }
            if (repository.existsBySanctionedName(entity.getSanctionedName())) {
                return null; // Skip duplicates
            }
            return entity;
        };
    }

    // Writer
    @Bean
    public ItemWriter<SanctionedEntity> writer(SanctionedEntityRepository repository) {
        return repository::saveAll;
    }

    // CSV Step
    @Bean
    public Step importCsvStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                              @Qualifier("csvReader") ItemReader<SanctionedEntity> csvReader,
                              ItemWriter<SanctionedEntity> writer,
                              ItemProcessor<SanctionedEntity, SanctionedEntity> processor) {
        return new StepBuilder("importCsvStep", jobRepository)
                .<SanctionedEntity, SanctionedEntity>chunk(100, transactionManager)
                .reader(csvReader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    // XML Step
    @Bean
    public Step importXmlStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                              @Qualifier("xmlReader") ItemReader<SanctionedEntity> xmlReader,
                              ItemWriter<SanctionedEntity> writer,
                              ItemProcessor<SanctionedEntity, SanctionedEntity> processor) {
        return new StepBuilder("importXmlStep", jobRepository)
                .<SanctionedEntity, SanctionedEntity>chunk(100, transactionManager)
                .reader(xmlReader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    // Batch Job
    @Bean
    public Job importSanctionsJob(JobRepository jobRepository, Step importCsvStep, Step importXmlStep) {
        return new JobBuilder("importSanctionsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(importCsvStep)
                .next(importXmlStep)
                .build();
    }

    // Transaction Manager
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
