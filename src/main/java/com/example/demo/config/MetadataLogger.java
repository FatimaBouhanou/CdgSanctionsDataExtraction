package com.example.demo.config;

import com.example.demo.service.ImportHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetadataLogger {

    private final ImportHistoryService importHistoryService;

    public void log(String url) {
        importHistoryService.logImportMetadata(url);
    }
}

