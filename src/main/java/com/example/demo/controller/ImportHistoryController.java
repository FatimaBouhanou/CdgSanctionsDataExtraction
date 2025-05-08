package com.example.demo.controller;

import com.example.demo.model.ImportHistory;
import com.example.demo.service.ImportHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/import-history")
@RequiredArgsConstructor
public class ImportHistoryController {

    private final ImportHistoryService importHistoryService;


    //adding import history
    @PostMapping("/log-all")
    public ResponseEntity<String> logAllImports() {
        importHistoryService.logAllImports();
        return ResponseEntity.ok("   All import metadata logged.");
    }

    //display of the history
    @GetMapping("/list")
    public ResponseEntity<List<ImportHistory>> getAllImports(){
        List<ImportHistory> historyList= importHistoryService.getAllImportHistory();
        return ResponseEntity.ok(historyList);
    }

}
