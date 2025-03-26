package com.example.demo.repository;

import com.example.demo.model.SanctionedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SanctionedEntityRepository extends JpaRepository<SanctionedEntity, Long> {

    Optional<SanctionedEntity> findBySanctionedName(String sanctionedName);

    // Exact match first
    List<SanctionedEntity> findBySanctionedNameIgnoreCase(String sanctionedName);

    // Partial match but sorted by name length (shorter names first)
    @Query("SELECT e FROM SanctionedEntity e WHERE LOWER(e.sanctionedName) LIKE LOWER(CONCAT('%', :sanctionedName, '%')) ORDER BY LENGTH(e.sanctionedName)")
    List<SanctionedEntity> findByNameSimilar(String sanctionedName);

    boolean existsBySanctionedName(String sanctionedName);

    List<SanctionedEntity> findBySanctionCountryIgnoreCase(String sanctionCountry);

    // Correct the method signature here
    List<SanctionedEntity> findBySanctionTypeIgnoreCase(String sanctionType);
}

