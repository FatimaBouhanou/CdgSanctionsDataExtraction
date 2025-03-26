package com.example.demo.repository;

import com.example.demo.model.DataSourceEntity;
import com.example.demo.model.DataType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface DataSourceRepository extends JpaRepository<DataSourceEntity, Long> {

    List<DataSourceEntity> findByEnabledTrue();

    @Transactional(readOnly = true)
    Optional<DataSourceEntity> findFirstByDataTypeAndEnabledTrueOrderByIdDesc(DataType dataType);
}
