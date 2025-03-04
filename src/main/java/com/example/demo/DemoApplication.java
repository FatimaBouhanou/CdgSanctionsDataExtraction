package com.example.demo;

import com.example.demo.service.EUSanctionsFetcher;
import com.example.demo.service.OFACSanctionFetcher;
import com.example.demo.service.UKSanctionsFetcher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value={UKSanctionsFetcher.class, EUSanctionsFetcher.class}))
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
