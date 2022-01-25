package com.base.batchproject.main.job;

import com.base.batchproject.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class UserManageJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final UserRepository userRepository;

    private String JOBNAME = "UserManageJob";

    @Bean
    public Job userJob(){
        return jobBuilderFactory.get(JOBNAME)
                .incrementer(new RunIdIncrementer())
                .start(this.userStep1())
                .build();
    }

    @Bean
    public Step userStep1(){
        return stepBuilderFactory.get(JOBNAME+"_step1")
                .tasklet(new UserSaveTasklet(userRepository))
                .build();
    }
}
