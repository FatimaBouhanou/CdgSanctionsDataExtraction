package com.example.demo.scheduler;

import com.example.demo.extractors.XmlDataExtractor;
import com.example.demo.model.SanctionedEntity;
import com.example.demo.repository.SanctionedEntityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataIngestionScheduler {

    private final XmlDataExtractor xmlDataExtractor;
    private final SanctionedEntityRepository sanctionedEntityRepository;

    @Autowired
    public DataIngestionScheduler(XmlDataExtractor xmlDataExtractor, SanctionedEntityRepository sanctionedEntityRepository) {
        this.xmlDataExtractor = xmlDataExtractor;
        this.sanctionedEntityRepository = sanctionedEntityRepository;
    }

    // Run every 10 minutes to fetch and store new data
    @Scheduled(cron = "0 */10 * * * *")  // Every 10 minutes
    @Transactional
    public void fetchAndStoreData() {
        try {
            String source = "https://www.treasury.gov/ofac/downloads/sdn.xml";  // OFAC URL
            List<SanctionedEntity> entities = xmlDataExtractor.extracData(source);

            // Loop through the entities and save to the repository
            for (SanctionedEntity entity : entities) {
                if (!sanctionedEntityRepository.findByName(entity.getName()).isPresent()) {
                    sanctionedEntityRepository.save(entity);
                    System.out.println("Saved: " + entity.getName());
                } else {
                    System.out.println("Skipped (Already exists): " + entity.getName());
                }
            }

            System.out.println("OFAC sanctions list updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
