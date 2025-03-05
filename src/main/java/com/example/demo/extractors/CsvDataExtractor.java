package com.example.demo.extractors;

import com.example.demo.model.SanctionedEntity;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CsvDataExtractor implements DataExtractor {

    @Override
    public List<SanctionedEntity> extracData(String source) throws IOException {
       List<SanctionedEntity> entities = new ArrayList<>();
       try(Reader reader = new FileReader(source);
           CSVParser csvParser= new CSVParser(reader, CSVFormat.DEFAULT.withHeader())){

           for (CSVRecord record : csvParser) {
               SanctionedEntity entity = new SanctionedEntity();
               entity.setName(record.get("Name"));
               entity.setCountry(record.get("Country"));
               entity.setSanctionList(record.get("SanctionList"));
               entity.setReason(record.get("Reason"));
               entities.add(entity);

           }
       }

        return entities;
    }
}
