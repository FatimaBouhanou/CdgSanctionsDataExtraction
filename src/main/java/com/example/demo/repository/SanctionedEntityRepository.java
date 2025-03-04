package com.example.demo.repository;

import com.example.demo.model.SanctionedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SanctionedEntityRepository extends JpaRepository<SanctionedEntity,Long> {


    Optional<SanctionedEntity> findByName(String name);}
