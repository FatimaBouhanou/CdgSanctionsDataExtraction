package com.example.demo.extractors;

import com.example.demo.model.SanctionedEntity;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class XmlExtractor implements DataExtractor {
    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    public List<SanctionedEntity> extracData(String source) throws IOException {
        return Arrays.asList(xmlMapper.readValue(new File(source),SanctionedEntity[].class));
    }
}
