package com.base.batchproject.main.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class TestTaskChunkConfigJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private String JOBNAME = "testTaskChunkJob";

    @Bean
    public Job testTaskChunkJob(){
        return jobBuilderFactory.get(JOBNAME)
                .incrementer(new RunIdIncrementer())
                .start(this.taskStep1())
                .next(this.chunkStep1())
                .next(this.taskPagingStep1())
                .build();
    }

    @Bean
    public Step taskStep1(){
        return stepBuilderFactory.get("taskStep1")
                .tasklet(tasklet())
                .build();
    }

    private Tasklet tasklet(){ //tasklet으로 모두 처리
        return (contribution, chunkContext) -> {
            List<String> items = getItems();
            log.info("items : "+ items.size());

            return RepeatStatus.FINISHED;
        };
    }
    @Bean
    public Step chunkStep1() { //chunksize로 처리
        return stepBuilderFactory.get("chunkStep1")
                .<String,String>chunk(10)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    private ItemProcessor<String, String> itemProcessor() {
        return item -> item + " now processor!!";
    }

    private ItemWriter<String> itemWriter() {
        return items -> log.info("### writer : " + items.size());
    }

    private ItemReader<String> itemReader() {
        return new ListItemReader<>(getItems());
    }

    @Bean
    public Step taskPagingStep1() { //tasklet으로 paging처리
        return stepBuilderFactory.get("taskPagingStep1")
                .tasklet(pagingTasklet())
                .build();
    }

    private Tasklet pagingTasklet() {
        List<String> items = getItems();

        return (contribution, chunkContext) -> {
            //stepexecution : 읽은 item을 저장
            StepExecution stepExecution = contribution.getStepExecution();
            int chunksize = 10;
            int readCnt = stepExecution.getReadCount();
            int idx = readCnt + chunksize;

            if(idx >= items.size()){
                return RepeatStatus.FINISHED;
            }

            //sublist : list 중간 데이터 읽기
            List<String> sublist = items.subList(readCnt,idx);

            log.info("### sublist size : " + sublist.size());
            stepExecution.setReadCount(idx); //read count 읽은만큼 다시 저장

            return RepeatStatus.CONTINUABLE;
        };
    }

    private List<String> getItems() {
        List<String> items = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            items.add(i + " test!");
        }

        return items;
    }

}
