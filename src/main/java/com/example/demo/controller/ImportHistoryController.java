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

    @PostMapping("/log")
    public ResponseEntity<String> logImport(@RequestParam String url) {
        importHistoryService.logImportMetadata(url);
        return ResponseEntity.ok("✅ Import metadata logged for: " + url);
    }
}

