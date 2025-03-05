package com.example.demo.extractors;

import com.example.demo.model.SanctionedEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JsonDataExtractor implements DataExtractor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<SanctionedEntity> extracData(String source) throws IOException {



        return Arrays.asList(objectMapper.readValue(new File(source),SanctionedEntity[].class));
    }
}
