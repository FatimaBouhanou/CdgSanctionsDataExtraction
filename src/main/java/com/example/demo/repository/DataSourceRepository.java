package com.example.demo.repository;

import com.example.demo.model.DataSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.sql.DataSource;
import java.util.List;

public interface DataSourceRepository extends JpaRepository<DataSourceEntity, Long> {
List<DataSourceEntity> findByEnabledTrue();

}
