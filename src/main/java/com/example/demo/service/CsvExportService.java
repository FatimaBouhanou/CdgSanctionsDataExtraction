package com.example.demo.service;

import com.example.demo.model.SanctionedEntity;
import com.example.demo.repository.SanctionedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class CsvExportService {

    private final SanctionedEntityRepository repository;

    @Autowired
    public CsvExportService(SanctionedEntityRepository repository) {
        this.repository = repository;
    }

    public String exportToCsv(String filePath) {
        List<SanctionedEntity> entities = repository.findAll();

        if (entities.isEmpty()) {
            return "No data available to export.";
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write the header
            writer.append("ID,country,last_name,reason,sanction_list,sanction_type,uid\n");

            // Write the data rows
            for (SanctionedEntity entity : entities) {
                writer.append(safeValue(entity.getId()))
                        .append(",")
                        .append(safeValue(entity.getCountry()))
                        .append(",")
                        .append(safeValue(entity.getName()))
                        .append(",")
                        .append(safeValue(entity.getReason()))
                        .append(",")
                        .append(safeValue(entity.getSanctionList()))
                        .append(",")
                        .append(safeValue(entity.getSdnType()))  // Added sanction type
                        .append(",")
                        .append(safeValue(entity.getUid()))
                        .append("\n");
            }

            return "CSV export successful: " + filePath;
        } catch (IOException e) {
            return "Error exporting CSV: " + e.getMessage();
        }
    }

    // Handle null values and escape commas by wrapping in quotes
    private String safeValue(Object value) {
        if (value == null) {
            return "";
        }
        String strValue = value.toString().replace("\"", "\"\"");
        return "\"" + strValue + "\"";  // Wrap in quotes to prevent CSV issues
    }
}
