package com.example.demo.factory;

import com.example.demo.extractors.CsvDataExtractor;
import com.example.demo.extractors.DataExtractor;
import org.springframework.stereotype.Component;

@Component
public class DataExtractorFactory {

    private final CsvDataExtractor csvDataExtractor;

    public DataExtractorFactory(CsvDataExtractor csvDataExtractor) {
        this.csvDataExtractor = csvDataExtractor;
    }

    public DataExtractor getExtractor(String sourceUrl) {
        if (sourceUrl.endsWith(".csv")) {
            return csvDataExtractor;
        }
        throw new IllegalArgumentException("Unsupported file type: " + sourceUrl);
    }
}
