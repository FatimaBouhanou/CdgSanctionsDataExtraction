package com.example.demo;


import com.example.demo.scheduler.DataIngestionScheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
//prevents DataIngestionScheduler from being executed
@ComponentScan(basePackages = "com.example.demo", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = DataIngestionScheduler.class))
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
