package com.base.batchproject.main.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.annotation.BeforeStep;

@Slf4j
public class TestJobListener {

    public static class jobExecutionListener implements JobExecutionListener{

        @Override
        public void beforeJob(JobExecution jobExecution) {
            log.info("before job!");
        }

        @Override
        public void afterJob(JobExecution jobExecution) {
            int writeSum = jobExecution.getStepExecutions().stream()
                            .mapToInt(StepExecution::getWriteCount).sum();
            log.info("after job!" + writeSum);
        }
    }

    public static class jobExecutionAnotListener{
        @BeforeJob
        public void beforeJob(JobExecution jobExecution) {
            log.info("annotation before job!");
        }

        @AfterJob
        public void afterJob(JobExecution jobExecution) {
            int writeSum = jobExecution.getStepExecutions().stream()
                    .mapToInt(StepExecution::getWriteCount).sum();
            log.info("annotation after job!" + writeSum);
        }
    }

    public static class stepExecutionAnoListener{
        @BeforeStep
        public void beforeStep(StepExecution stepExecution){
            log.info("before step!!");
        }

        @AfterStep
        public ExitStatus afterStep(StepExecution stepExecution){
            log.info("after step!! : {}", stepExecution.getWriteCount());

            return stepExecution.getExitStatus(); //step실행의 완료/실패여부
        }
    }

}
