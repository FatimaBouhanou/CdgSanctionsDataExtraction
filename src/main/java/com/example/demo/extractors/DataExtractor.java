package com.example.demo.extractors;

import com.example.demo.model.SanctionedEntity;

import java.util.List;

public interface DataExtractor {
    List<SanctionedEntity> extractData(String source) throws Exception;  // Add `throws Exception`
}
