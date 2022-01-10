package com.base.batchproject.main.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class TestConfigJob {

    private final JobBuilderFactory jobBuilderFactory; //job관리해주는 팩토리. 이미 bean으로 생성됨.
    private final StepBuilderFactory stepBuilderFactory;

    private String JOBNAME = "testJob";

    @Bean
    public Job testJob(){
        return jobBuilderFactory.get(JOBNAME)
                .incrementer(new RunIdIncrementer()) //job실행할떄마다 파라미터 id를 자동생성
                .start(this.step1()) //최초 실행 step
                .build();
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .tasklet((contribution, chunkContext) -> {
                    log.info("step1 start!");

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
