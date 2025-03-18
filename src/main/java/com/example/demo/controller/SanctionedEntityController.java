package com.example.demo.controller;

import com.example.demo.model.SanctionedEntity;
import com.example.demo.service.SanctionedEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sanctions")
public class SanctionedEntityController {
    @Autowired
    private SanctionedEntityService service;

    @GetMapping(value = "/search", produces = "application/json")
    public List<SanctionedEntity> search(@RequestParam String name) {
        return service.searchEntitiesByName(name);
    }
}


