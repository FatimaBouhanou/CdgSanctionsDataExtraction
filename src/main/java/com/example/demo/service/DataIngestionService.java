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


    public void ingestData(String source) throws IOException{
        DataExtractor extractor = extractorFactory.getExtractor(source);
        List<SanctionedEntity> entities= extractor.extracData(source);
        repository.saveAll(entities);
    }
}
