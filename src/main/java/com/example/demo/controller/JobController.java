package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor // Lombok annotation for constructor injection
public class JobController {

    private final JobOperator jobOperator;
    private final JobExplorer jobExplorer;

    @PostMapping(value = "/stop/{executionId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> stopJob(
            @PathVariable long executionId) {

        Map<String, Object> response = new HashMap<>();

        try {
            boolean stopped = jobOperator.stop(executionId);
            response.put("success", stopped);
            response.put("executionId", executionId);

            return stopped
                    ? ResponseEntity.ok(response)
                    : ResponseEntity.status(HttpStatus.CONFLICT).body(response);

        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}