package com.example.demo.controller;

import com.example.demo.service.CsvExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsvExportController {

    private final CsvExportService csvExportService;

    @Autowired
    public CsvExportController(CsvExportService csvExportService) {
        this.csvExportService = csvExportService;
    }

    @GetMapping("/export/csv")
    public String exportCsv(@RequestParam(defaultValue = "sanctions.csv") String filePath) {
        return csvExportService.exportToCsv(filePath);
    }
}
