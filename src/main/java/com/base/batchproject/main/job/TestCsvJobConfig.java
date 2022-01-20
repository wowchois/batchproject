package com.base.batchproject.main.job;

import com.base.batchproject.main.common.TestJobListener;
import com.base.batchproject.main.vo.TestCsvFeildVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class TestCsvJobConfig {

    private static final String JOB_ID = "TestCsvJobConfig";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean(JOB_ID)
    public Job job() throws Exception{
        return jobBuilderFactory.get(JOB_ID)
                .listener(new TestJobListener.jobExecutionListener())
                .start(step1())
                .build();

    }

    @Bean(JOB_ID + "_step1")
    @JobScope
    public Step step1() throws Exception{

        return stepBuilderFactory.get(JOB_ID + "step1")
                .<TestCsvFeildVo, TestCsvFeildVo>chunk(10)
                .reader(csvFileReader())
                .writer(writer())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<TestCsvFeildVo> csvFileReader() throws Exception {
        DefaultLineMapper<TestCsvFeildVo> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();

        tokenizer.setNames("id","name","address");
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSet -> {
            int id = fieldSet.readInt("id"); //id 읽기
            String name = fieldSet.readString("name");
            String address = fieldSet.readString("address");

            return new TestCsvFeildVo(id,name,address);
        });

        FlatFileItemReader reader = new FlatFileItemReaderBuilder<TestCsvFeildVo>()
                .name("csvItemReader")
                .encoding("UTF-8")
                .resource(new ClassPathResource("infotemp.csv")) //ClassPathResource : resources 파일 밑 디렉토리를 읽는 클래스
                .linesToSkip(1) //1번쨰 필드명 부분 skip
                .lineMapper(lineMapper)
                .build();

        //itemreader에서 필요한 설정들이 제대로 됬는지 검증하는 함수 (throw Exception으로 넘김)
        reader.afterPropertiesSet();

        return reader;
    }

    @Bean
    @StepScope
    public ItemWriter<TestCsvFeildVo> writer(){
        return items -> {
            items.forEach(vo -> {
                log.info("### id : " + vo.getId());
                log.info("### name : " + vo.getName());
                log.info("### address : " + vo.getAddress());
            });
        };
    }

}
