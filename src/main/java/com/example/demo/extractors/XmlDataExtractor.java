package com.example.demo.extractors;

import com.example.demo.model.SanctionedEntity;
import com.example.demo.model.SanctionedEntityWrapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Component
public class XmlDataExtractor implements DataExtractor {

    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    public List<SanctionedEntity> extracData(String source) {
        try {
            URL url = new URL(source);
            try (InputStream inputStream = url.openStream()) {
                // Parse the XML into the wrapper class
                SanctionedEntityWrapper wrapper = xmlMapper.readValue(inputStream, SanctionedEntityWrapper.class);
                return wrapper.getSdnEntries();  // Return the list of SanctionedEntities
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Return null or handle error appropriately
        }
    }
}
