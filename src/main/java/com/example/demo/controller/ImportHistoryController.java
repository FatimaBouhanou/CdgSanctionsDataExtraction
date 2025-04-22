package com.example.demo.controller;

import com.example.demo.service.ImportHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/import-history")
@RequiredArgsConstructor
public class ImportHistoryController {

    private final ImportHistoryService importHistoryService;

    @PostMapping("/log-all")
    public ResponseEntity<String> logAllImports() {
        importHistoryService.logAllImports();
        return ResponseEntity.ok("✅ All import metadata logged.");
    }

}
