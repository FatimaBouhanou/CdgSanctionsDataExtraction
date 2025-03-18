package com.example.demo.factory;

import com.example.demo.extractors.CsvDataExtractor;

import com.example.demo.extractors.DataExtractor;
import com.example.demo.extractors.XmlDataExtractor;
import org.springframework.stereotype.Component;

@Component
public class DataExtractorFactory {

    private final XmlDataExtractor xmlDataExtractor;
    private final CsvDataExtractor csvDataExtractor;

    public DataExtractorFactory(XmlDataExtractor xmlDataExtractor, CsvDataExtractor csvDataExtractor) {
        this.xmlDataExtractor = xmlDataExtractor;
        this.csvDataExtractor = csvDataExtractor;
    }

    public DataExtractor getExtractor(String sourceUrl) {
        if (sourceUrl.endsWith(".xml")) {
            return xmlDataExtractor;
        } else if (sourceUrl.endsWith(".csv")) {
            return csvDataExtractor;
        }
        throw new IllegalArgumentException("Unsupported file type: " + sourceUrl);
    }
}

