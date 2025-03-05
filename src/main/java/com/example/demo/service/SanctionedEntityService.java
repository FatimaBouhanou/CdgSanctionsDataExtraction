package com.example.demo.service;

import com.example.demo.model.SanctionedEntity;
import com.example.demo.repository.SanctionedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SanctionedEntityService {
    @Autowired
    private SanctionedEntityRepository repository;

    public List<SanctionedEntity> searchEntitiesByName(String name) {
        List<SanctionedEntity> exactMatches = repository.findByNameIgnoreCase(name);
        List<SanctionedEntity> similarMatches = repository.findByNameSimilar(name);

        // Merge results: exact matches first, then similar matches (removing duplicates)
        return Stream.concat(exactMatches.stream(), similarMatches.stream())
                .distinct()  // Avoid duplicates
                .collect(Collectors.toList());
    }
}


