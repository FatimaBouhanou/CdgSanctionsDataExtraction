package com.example.demo.repository;

import com.example.demo.model.SanctionedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SanctionedEntityRepository extends JpaRepository<SanctionedEntity,Long> {


    Optional<SanctionedEntity> findByName(String name);

    // Exact match first
    List<SanctionedEntity> findByNameIgnoreCase(String name);

    // Partial match but sorted by name length (shorter names first)
    @Query("SELECT e FROM SanctionedEntity e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY LENGTH(e.name)")
    List<SanctionedEntity> findByNameSimilar(String name);
}
