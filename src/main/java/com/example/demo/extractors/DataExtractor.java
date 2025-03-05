package com.example.demo.extractors;

import com.example.demo.model.SanctionedEntity;

import java.io.IOException;
import java.util.List;

public interface DataExtractor {
    List<SanctionedEntity> extracData(String source) throws Exception;  // Add `throws Exception`
}
