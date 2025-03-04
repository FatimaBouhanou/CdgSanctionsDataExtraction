package com.example.demo.service;

import com.example.demo.model.SanctionedEntity;
import com.example.demo.repository.SanctionedEntityRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;

@Service
public class OFACSanctionFetcher {
    private static final String OFAC_URL = "https://www.treasury.gov/ofac/downloads/sdn.xml";

    @Autowired
    private SanctionedEntityRepository repository;

    // ✅ Run once when the application starts
    @PostConstruct
    public void init() {
        fetchAndStoreOFACSanction();
    }

    // ✅ Run every 30 seconds for testing (change later)
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void fetchAndStoreOFACSanction() {
        try {
            System.out.println("Fetching OFAC sanctions data...");
            URL url = new URL(OFAC_URL);
            InputStream inputStream = url.openStream();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList entries = document.getElementsByTagName("sdnEntry");
            System.out.println("Total Entries Found: " + entries.getLength());

            for (int i = 0; i < entries.getLength(); i++) {
                Element element = (Element) entries.item(i);
                Node lastNameNode = element.getElementsByTagName("lastName").item(0);
                if (lastNameNode == null) continue;
                String name = lastNameNode.getTextContent();

                NodeList addressList = element.getElementsByTagName("address");
                String country = "Unknown";
                if (addressList.getLength() > 0) {
                    Element addressElement = (Element) addressList.item(0);
                    Node countryNode = addressElement.getElementsByTagName("country").item(0);
                    if (countryNode != null) {
                        country = countryNode.getTextContent();
                    }
                }

                System.out.println("Extracted Data -> Name: " + name + ", Country: " + country);

                if (!repository.findByName(name).isPresent()) {
                    SanctionedEntity entity = new SanctionedEntity();
                    entity.setName(name);
                    entity.setCountry(country);
                    entity.setSanctionList("OFAC");
                    entity.setReason("Sanctioned by OFAC");

                    repository.save(entity);
                    System.out.println("Saved: " + name);
                } else {
                    System.out.println("Skipped (Already exists): " + name);
                }
            }

            // ✅ Dummy record for testing
           /* SanctionedEntity testEntity = new SanctionedEntity();
            testEntity.setName("Test Person");
            testEntity.setCountry("Test Country");
            testEntity.setSanctionList("Test List");
            testEntity.setReason("Testing Insert");

            repository.save(testEntity);
            System.out.println("✅ Test Insert Completed!");*/

            System.out.println("OFAC sanctions list updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
