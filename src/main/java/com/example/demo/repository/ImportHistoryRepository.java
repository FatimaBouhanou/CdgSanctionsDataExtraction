package com.example.demo.repository;

import com.example.demo.model.ImportHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportHistoryRepository extends JpaRepository<ImportHistory, Long> {
}
