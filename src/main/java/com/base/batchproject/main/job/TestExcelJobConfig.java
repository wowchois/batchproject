package com.base.batchproject.main.job;

import com.base.batchproject.main.common.TestExcelStepListener;
import com.base.batchproject.main.common.TestJobListener;
import com.base.batchproject.main.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.*;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class TestExcelJobConfig {
    private static final String JOB_ID = "TestExcelJobConfig";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final TestExcelStepListener excelListener;

    private final int chunkSize = 100;
    private int rowIdx = 0;

    @Bean(JOB_ID)
    public Job job() throws Exception{
        return jobBuilderFactory.get(JOB_ID)
                .incrementer(new RunIdIncrementer())
                .listener(new TestJobListener.jobExecutionListener())
                .start(excelStep1())
                .build();

    }

    @Bean
    @JobScope
    public Step excelStep1() throws Exception{
        return stepBuilderFactory.get(JOB_ID + "_step1")
                .<User, User>chunk(chunkSize)
                .reader(excelReader1())
                .writer(excelWriter1())
                .listener(excelListener)
                .build();
    }

    //엑셀 저장할 데이터 read
    private ItemReader<? extends User> excelReader1() throws Exception{
        JpaPagingItemReader reader = new JpaPagingItemReaderBuilder<User>()
                .pageSize(chunkSize)
                .entityManagerFactory(entityManagerFactory)
                .queryString("select id,username from User")
                .name("getTargetReader")
                .build();

        reader.afterPropertiesSet();
        return reader;
    }

    //create excel file
    private ItemWriter<User> excelWriter1() {
        return items -> {
            log.info("### writer ### {}", items.get(0));

            //List<String[]> te = (List<String[]>) items.stream().map(s -> new String[] {}).collect(Collectors.toList());

            Row row = null;
            Cell cell = null;
            Iterator e = items.iterator();
            while(e.hasNext()){
                row = excelListener.sheet.createRow(rowIdx++);
                Object[] t = (Object[]) e.next();

                for(int i=0;i<Arrays.stream(t).count(); i++){
                    cell = row.createCell(i);
                    cell.setCellValue(t[i].toString());
                }
            }
        };
    }

}
