package com.base.batchproject.job;

import com.base.batchproject.main.job.UserManageJob;
import com.base.batchproject.main.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

@SpringBatchTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {UserManageJob.class}) //test대상, test config
public class UserManageJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void test() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //update한 날짜가 오늘인 데이터만 확인
        int size = userRepository.findAllByUpdateDate(LocalDate.now()).size();

        jobExecution.getStepExecutions().stream()
                        .forEach(x -> System.out.print(x.getStepName()));
/*
        Assertions.assertThat(jobExecution.getStepExecutions().stream()
                .filter(x -> x.getStepName().equals("gradeManageStep2"))
                .mapToInt(StepExecution::getWriteCount)
                .sum()
        ).isEqualTo(size).isEqualTo(300); //write count가 300건인지 확인
*/
        //400건 모두 insert 되었는지
        Assertions.assertThat(userRepository.count()).isEqualTo(400);

    }

}
