package com.example.demo.extractors;

import com.example.demo.model.SanctionedEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class ApiDataExtractor implements DataExtractor {
    private final WebClient webClient = WebClient.create();
    @Override
    public List<SanctionedEntity> extracData(String source) throws IOException {
        String jsonData = webClient.get().uri(source).retrieve().bodyToMono(String.class).block();
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            return Arrays.asList(objectMapper.readValue(jsonData, SanctionedEntity[].class));
        }catch(IOException e) {
            throw new RuntimeException("Error parsing api Response",e);
        }
    }
}
