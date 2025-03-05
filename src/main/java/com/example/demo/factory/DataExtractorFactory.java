package com.example.demo.factory;

import com.example.demo.extractors.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class DataExtractorFactory {
    private final List<DataExtractor> extractors;

    @Autowired
    public DataExtractorFactory(List<DataExtractor> extractors) {
        this.extractors = extractors;
    }

    public DataExtractor getExtractor(String source) {
        if (source.endsWith(".csv")) {
            return extractors.stream().filter(e -> e instanceof CsvDataExtractor).findFirst().orElseThrow();
        } else if (source.endsWith(".json")) {
            return extractors.stream().filter(e -> e instanceof JsonDataExtractor).findFirst().orElseThrow();
        } else if (source.endsWith(".xml")) {
            return extractors.stream().filter(e -> e instanceof XmlDataExtractor).findFirst().orElseThrow();
        } else if (source.startsWith("http")) {
            return extractors.stream().filter(e -> e instanceof ApiDataExtractor).findFirst().orElseThrow();
        }
        throw new IllegalArgumentException("Unsupported data format: " + source);
    }
}

