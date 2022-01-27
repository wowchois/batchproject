package com.base.batchproject.main.job;

import com.base.batchproject.main.entity.User;
import com.base.batchproject.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
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

@RequiredArgsConstructor
@Configuration
@Slf4j
public class UserManageJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private final UserRepository userRepository;

    private String JOBNAME = "UserManageJob";
    private int chunkSize = 100;

    @Bean
    public Job userJob() throws Exception{
        return jobBuilderFactory.get(JOBNAME)
                .incrementer(new RunIdIncrementer())
                .start(this.userStep1())
                .next(this.gradeManageStep2())
                .build();
    }

    @Bean
    public Step userStep1(){
        return stepBuilderFactory.get(JOBNAME+"_step1")
                .tasklet(new UserSaveTasklet(userRepository))
                .build();
    }

    @Bean
    public Step gradeManageStep2() throws Exception{
        return stepBuilderFactory.get(JOBNAME+"_step2")
                .<User, User>chunk(chunkSize)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    private ItemReader<? extends User> itemReader() throws Exception{
        JpaPagingItemReader<User> reader = new JpaPagingItemReaderBuilder<User>()
                .queryString("select * from User")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .name("userItemReader")
                .build();

        reader.afterPropertiesSet();

        return reader;
    }

    private ItemProcessor<? super User,? extends User> itemProcessor() {
        return user -> {
            if(user.targetUpgrade()){
                user.upGrade();
                return user;
            }
            return null;
        };
    }

    private ItemWriter<? super User> itemWriter() {
        return user -> {
            user.forEach(t -> {
                userRepository.save(t);
            });
        };
    }


}
