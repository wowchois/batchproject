package com.base.batchproject;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Profile;

@Profile("mysql")
@EnableBatchProcessing //spring batch 사용 설정
@SpringBootApplication //(exclude = DataSourceAutoConfiguration.class)
public class BatchprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchprojectApplication.class, args);
    }

}
