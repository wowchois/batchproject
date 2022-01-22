package com.base.batchproject.main.job;

import com.base.batchproject.main.common.TestJobListener;
import com.base.batchproject.main.vo.TestCsvFieldVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

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
                .<TestCsvFieldVo, TestCsvFieldVo>chunk(10)
                .reader(csvFileReader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<TestCsvFieldVo> csvFileReader() throws Exception {
        DefaultLineMapper<TestCsvFieldVo> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();

        tokenizer.setNames("id","name","address");
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSet -> {
            int id = fieldSet.readInt("id"); //id 읽기
            String name = fieldSet.readString("name");
            String address = fieldSet.readString("address");

            return new TestCsvFieldVo(id,name,address);
        });

        FlatFileItemReader reader = new FlatFileItemReaderBuilder<TestCsvFieldVo>()
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
    public ItemProcessor<TestCsvFieldVo, TestCsvFieldVo> processor(){
        return new ItemProcessor<TestCsvFieldVo, TestCsvFieldVo>() {
            @Override
            public TestCsvFieldVo process(TestCsvFieldVo item) throws Exception {

                item.setAddress(item.getAddress() + "_process");

                log.info("### id : " + item.getId());
                log.info("### name : " + item.getName());
                log.info("### address : " + item.getAddress());

                return item;
            }
        };
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<TestCsvFieldVo> writer() throws Exception{
        //csv파일에 작성할 데이터를 추출하기 위해서 feildExtractor 객체가 필요
        BeanWrapperFieldExtractor<TestCsvFieldVo> extractor = new BeanWrapperFieldExtractor();
        extractor.setNames(new String[] {"id","name","address"}); //필드명 설정

        //line 구분값 설정
        DelimitedLineAggregator<TestCsvFieldVo> lineAggreator = new DelimitedLineAggregator<>();
        lineAggreator.setDelimiter(",");
        lineAggreator.setFieldExtractor(extractor);

        FlatFileItemWriter<TestCsvFieldVo> writer = new FlatFileItemWriterBuilder<TestCsvFieldVo>()
                .name("csvItemWriter")
                .encoding("UTF-8")
                .resource(new FileSystemResource("output/test.csv")) //FileSystemResource : write할때 경로 지정
                .lineAggregator(lineAggreator)
                .headerCallback(writer1 -> writer1.write("id,name,address")) //header설정
                .footerCallback(writer1 -> writer1.write("---------------\n")) //footer설정
                .append(true)
                .build();

        writer.afterPropertiesSet();

        return writer;
    }

}
