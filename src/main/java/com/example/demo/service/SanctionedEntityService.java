package com.example.demo.service;

import com.example.demo.model.SanctionedEntity;
import com.example.demo.repository.SanctionedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Service
public class SanctionedEntityService {
    @Autowired
    private SanctionedEntityRepository repository;

    public List<SanctionedEntity> searchEntities(String name, String country, String uid, String sanctionType) {
        List<SanctionedEntity> results = Collections.emptyList(); // Default empty list

        if (name != null && !name.isEmpty()) {
            results = searchEntitiesByName(name);
        } else if (country != null && !country.isEmpty()) {
            results = repository.findByCountryIgnoreCase(country);
        } else if (sanctionType != null && !sanctionType.isEmpty()) {
            results = repository.findBySdnTypeIgnoreCase(sanctionType);
        } else {
            results = repository.findAll();
        }

        return results.isEmpty() ? Collections.emptyList() : results;
    }

    private List<SanctionedEntity> searchEntitiesByName(String name) {
        List<SanctionedEntity> exactMatches = repository.findByNameIgnoreCase(name);
        List<SanctionedEntity> similarMatches = repository.findByNameSimilar(name);

        List<SanctionedEntity> combinedResults = Stream.concat(exactMatches.stream(), similarMatches.stream())
                .distinct()
                .toList();

        return combinedResults.isEmpty() ? Collections.emptyList() : combinedResults;
    }
}
