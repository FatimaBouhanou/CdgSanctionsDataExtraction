package com.example.demo.service;

import com.example.demo.model.SanctionedEntity;
import com.example.demo.repository.SanctionedEntityRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

@Service
public class UKSanctionsFetcher {
    private static final String UK_URL = "https://ofsistorage.blob.core.windows.net/publishlive/ConList.csv";

    @Autowired
    private SanctionedEntityRepository repository;

    @Scheduled(fixedRate = 30000) // Runs every day at noon
    public void fetchAndStoreUKSanctions() {

        try (InputStream inputStream = new URL(UK_URL).openStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
                String name = record.get("Name");
                String country = record.get("Country");

                if (!repository.findByName(name).isPresent()) {
                    SanctionedEntity entity = new SanctionedEntity();
                    entity.setName(name);
                    entity.setCountry(country);
                    entity.setSanctionList("UK");
                    entity.setReason("Sanctioned by UK");
                    repository.save(entity);
                }
            }
            System.out.println("UK sanctions list updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
