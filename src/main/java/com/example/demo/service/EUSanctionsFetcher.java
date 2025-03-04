package com.example.demo.service;

import com.example.demo.model.SanctionedEntity;
import com.example.demo.repository.SanctionedEntityRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;

@Service
public class EUSanctionsFetcher {
    private static final String API_URL = "https://sanctions.network/sanctions";

    @Autowired
    private SanctionedEntityRepository repository;

    @Scheduled(fixedRate = 86400000) // Runs daily
    public void fetchAndStoreSanctions() {
        try {
            System.out.println("Fetching sanctions data...");

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());

                JsonNode sanctions = rootNode.path("data"); // Adjust based on actual API response

                for (JsonNode node : sanctions) {
                    String name = node.path("name").asText();
                    String country = node.path("country").asText("Unknown");
                    String reason = node.path("reason").asText("Sanctioned");

                    if (!repository.findByName(name).isPresent()) {
                        SanctionedEntity entityToSave = new SanctionedEntity();
                        entityToSave.setName(name);
                        entityToSave.setCountry(country);
                        entityToSave.setSanctionList("Sanctions Network");
                        entityToSave.setReason(reason);
                        repository.save(entityToSave);
                        System.out.println("✅ Saved: " + name);
                    } else {
                        System.out.println("⏭️ Skipped (Already exists): " + name);
                    }
                }
                System.out.println("✅ Sanctions data updated successfully.");
            } else {
                System.out.println("❌ Failed to fetch sanctions: HTTP " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
