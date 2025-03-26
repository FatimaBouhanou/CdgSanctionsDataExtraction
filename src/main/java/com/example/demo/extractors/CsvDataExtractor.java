package com.example.demo.extractors;


import com.example.demo.model.SanctionedEntity;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvDataExtractor implements DataExtractor {

    public List<SanctionedEntity> extractData(String filePath) {
        List<SanctionedEntity> entities = new ArrayList<>();

        try (Reader reader = new FileReader(filePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                SanctionedEntity entity = new SanctionedEntity();

                entity.setSanctionedName(getSafeValue(csvRecord, "sanctionedName"));
                entity.setSanctionReason(getSafeValue(csvRecord, "sanctionReason"));
                entity.setSanctionList(getSafeValue(csvRecord, "sanctionList"));
                entity.setSanctionType(getSafeValue(csvRecord, "sdnType"));
                entity.setSanctionCountry(getSafeValue(csvRecord, "sanctionCountry", "Unknown"));

                entities.add(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entities;
    }

    private String getSafeValue(CSVRecord record, String columnName) {
        return record.isMapped(columnName) ? record.get(columnName) : "";
    }

    private String getSafeValue(CSVRecord record, String columnName, String defaultValue) {
        return record.isMapped(columnName) && !record.get(columnName).trim().isEmpty() ? record.get(columnName) : defaultValue;
    }


}
