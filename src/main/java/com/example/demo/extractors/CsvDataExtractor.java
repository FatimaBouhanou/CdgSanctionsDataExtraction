package com.example.demo.extractors;

import com.example.demo.model.SanctionedEntity;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvDataExtractor implements DataExtractor {

    public List<SanctionedEntity> extractData(String fileUrl) {
        List<SanctionedEntity> entities = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(fileUrl).openStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
                SanctionedEntity entity = new SanctionedEntity();
                entity.setId(record.get("id"));
                entity.setSchema(getSafeValue(record, "schema"));
                entity.setName(getSafeValue(record, "name"));
                entity.setAliases(getSafeValue(record, "aliases"));
                entity.setBirth_date(getSafeValue(record, "birthDate"));
                entity.setCountries(getSafeValue(record, "countries"));
                entity.setAddresses(getSafeValue(record, "addresses"));
                entity.setIdentifiers(getSafeValue(record,"identifiers"));
                entity.setSanctions(getSafeValue(record, "sanctions"));
                entity.setPhones(getSafeValue(record, "phones"));
                entity.setEmails(getSafeValue(record, "emails"));
                entity.setDataset(getSafeValue(record, "dataset"));
                entity.setFirst_seen(getSafeValue(record, "firstSeen"));
                entity.setLast_seen(getSafeValue(record, "lastSeen"));
                entity.setLast_change(getSafeValue(record, "lastChange"));

                entities.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return entities;
    }

    private String getSafeValue(CSVRecord record, String columnName) {
        return record.isMapped(columnName) ? record.get(columnName) : "";
    }

    private LocalDate parseDate(String value) {
        try {
            return value != null && !value.isBlank() ? LocalDate.parse(value.trim()) : null;
        } catch (DateTimeParseException e) {
            System.err.println("Could not parse date: " + value);
            return null;
        }
    }
}
