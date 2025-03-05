package com.example.demo.scheduler;

import com.example.demo.service.DataIngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DataIngestionScheduler {
    private final DataIngestionService ingestionService;

    @Autowired
    public DataIngestionScheduler(DataIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }


    @Scheduled(fixedRate = 30000)
    public void fetchData(){
        try{
            ingestionService.ingestData("https://www.treasury.gov/ofac/downloads/sdn.xml");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
