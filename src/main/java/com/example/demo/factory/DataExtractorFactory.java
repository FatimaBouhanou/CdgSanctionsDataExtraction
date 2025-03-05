package com.example.demo.factory;

import com.example.demo.extractors.XmlDataExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataExtractorFactory {

    private final XmlDataExtractor xmlDataExtractor;

    @Autowired
    public DataExtractorFactory(XmlDataExtractor xmlDataExtractor) {
        this.xmlDataExtractor = xmlDataExtractor;
    }

    public XmlDataExtractor getExtractor(String source) {
        // Check if the source is an XML file or URL
        if (source.endsWith(".xml") || source.startsWith("http")) {
            return xmlDataExtractor;
        }

        throw new IllegalArgumentException("Unsupported data format: " + source);
    }
}
