package com.example.demo.controller;

import com.example.demo.model.SanctionedEntity;
import com.example.demo.repository.SanctionedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class SanctionController {

    @Autowired
    private SanctionedEntityRepository repository;

    @GetMapping("/insert")
    public String insertTestEntity() {
        SanctionedEntity entity = new SanctionedEntity();
        entity.setName("Test Person");
        entity.setCountry("Test Country");
        entity.setSanctionList("Test List");
        entity.setReason("Testing Insert");

        repository.save(entity);
        return "✅ Test record inserted!";
    }
}
