package com.example.demo.extractors;

import com.example.demo.model.SanctionedEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class XmlDataExtractor implements DataExtractor {

    private final RestTemplate restTemplate;

    public XmlDataExtractor() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    @Override
    public List<SanctionedEntity> extractData(String source) {
        List<SanctionedEntity> sanctionedEntities = new ArrayList<>();

        try {
            System.out.println("Fetching XML from: " + source);
            ResponseEntity<String> response = restTemplate.getForEntity(source, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.err.println("Failed to fetch XML. Status: " + response.getStatusCode());
                return null;
            }

            // Convert response body to InputStream
            InputStream inputStream = new URL(source).openStream();

            // Parse XML using DOM Parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            // Extract all "sdnEntry" elements
            NodeList entries = document.getElementsByTagName("sdnEntry");
            System.out.println("Total Entries Found: " + entries.getLength());

            for (int i = 0; i < entries.getLength(); i++) {
                Element element = (Element) entries.item(i);

                // Extract last name
                Node lastNameNode = element.getElementsByTagName("lastName").item(0);
                if (lastNameNode == null) continue;
                String name = lastNameNode.getTextContent();

                // Extract country (from address node)
                NodeList addressList = element.getElementsByTagName("address");
                String country = "Unknown";
                if (addressList.getLength() > 0) {
                    Element addressElement = (Element) addressList.item(0);
                    Node countryNode = addressElement.getElementsByTagName("country").item(0);
                    if (countryNode != null) {
                        country = countryNode.getTextContent();
                    }
                }

                // Extract sanction type (from sdnType node)
                String sanctionType = "Unknown";
                Node sanctionTypeNode = element.getElementsByTagName("sdnType").item(0);
                if (sanctionTypeNode != null) {
                    sanctionType = sanctionTypeNode.getTextContent();
                }

                System.out.println("Extracted Data -> Name: " + name + ", Country: " + country + ", Sanction Type: " + sanctionType);

                // Create SanctionedEntity object
                SanctionedEntity entity = new SanctionedEntity();
                entity.setSanctionedName(name);
                entity.setSanctionCountry(country);
                entity.setSanctionList("OFAC");
                entity.setSanctionReason("Sanctioned by OFAC");
                entity.setSanctionType(sanctionType);  // Set the sanction type

                // Generate a unique UID (e.g., using UUID)
//                String uniqueUid = UUID.randomUUID().toString();
//                entity.setUid(uniqueUid);

                // Add to the list
                sanctionedEntities.add(entity);
            }

            System.out.println("✅ Extraction Completed: " + sanctionedEntities.size() + " records");
            return sanctionedEntities;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
