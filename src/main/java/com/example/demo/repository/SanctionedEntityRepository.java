package com.example.demo.repository;

import com.example.demo.model.SanctionedEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface SanctionedEntityRepository extends JpaRepository<SanctionedEntity, String> {

    Optional<SanctionedEntity> findByName(String sanctionedName);

    // Exact match by name (case insensitive)
    List<SanctionedEntity> findByNameIgnoreCase(String sanctionedName);

    // Partial match, sorted by shortest name first
    @Query("SELECT e FROM SanctionedEntity e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :sanctionedName, '%')) ORDER BY LENGTH(e.name)")
    List<SanctionedEntity> findByNameSimilar(String sanctionedName);

    boolean existsByName(String sanctionedName);

    // Match entities by one of the countries (case insensitive)
    @Query("SELECT e FROM SanctionedEntity e JOIN e.countries c WHERE LOWER(c) = LOWER(:country)")
    List<SanctionedEntity> findByCountryIgnoreCase(String country);

    // Match entities by one of the sanctions (case insensitive)
    @Query("SELECT e FROM SanctionedEntity e JOIN e.sanctions s WHERE LOWER(s) = LOWER(:sanctionType)")
    List<SanctionedEntity> findBySanctionsIgnoreCase(String sanctionType);
}
