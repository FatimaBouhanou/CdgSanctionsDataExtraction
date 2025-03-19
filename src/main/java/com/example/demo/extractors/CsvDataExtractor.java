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
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                SanctionedEntity entity = new SanctionedEntity();

                entity.setName(csvRecord.get("last_name"));
                entity.setReason(csvRecord.get("reason"));
                entity.setSanctionList(csvRecord.get("sanction_list"));
                entity.setUid(csvRecord.get("uid"));
                entity.setSdnType(csvRecord.get("sanction_type"));
                //handling null country value
                String country = csvRecord.get("country");
                if (country == null || country.trim().isEmpty()) {
                    country = "Unknown";
                }
                entity.setCountry(country);

                entities.add(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entities;
    }

}
