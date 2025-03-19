package com.example.demo.service;

import com.example.demo.model.SanctionedEntity;
import com.example.demo.repository.SanctionedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class SanctionedEntityService {
    @Autowired
    private SanctionedEntityRepository repository;

    public List<SanctionedEntity> searchEntities(String name, String country, String uid, String sanctionType) {
        if (name != null && !name.isEmpty()) {
            return searchEntitiesByName(name);
        } else if (country != null && !country.isEmpty()) {
            return repository.findByCountryIgnoreCase(country);
        } else if (sanctionType != null && !sanctionType.isEmpty()) {
            return repository.findBySdnTypeIgnoreCase(sanctionType);
        }
        //return all entities if no filter is applied
        return repository.findAll();
    }

    private List<SanctionedEntity> searchEntitiesByName(String name) {
        List<SanctionedEntity> exactMatches = repository.findByNameIgnoreCase(name);
        List<SanctionedEntity> similarMatches = repository.findByNameSimilar(name);

        return Stream.concat(exactMatches.stream(), similarMatches.stream())
                .distinct()
                .toList();
    }
}
