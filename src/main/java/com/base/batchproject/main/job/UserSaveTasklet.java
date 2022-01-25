package com.base.batchproject.main.job;

import com.base.batchproject.main.entity.User;
import com.base.batchproject.main.repository.UserRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//user저장을 위한 tasklet
public class UserSaveTasklet implements Tasklet {

    private final UserRepository userRepository;

    public UserSaveTasklet(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<User> users = createUser();

        Collections.shuffle(users);
        userRepository.saveAll(users);

        return RepeatStatus.FINISHED;
    }

    //test user create
    private List<User> createUser() {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < 100; i++) { //NORMAL
            users.add(User.builder()
                    .totalamt(1_000)
                    .username("user"+i)
                    .build());
        }
        for (int i = 100; i < 200; i++) { //NORMAL->SILVER
            users.add(User.builder()
                    .totalamt(200_000)
                    .username("user"+i)
                    .build());
        }
        for (int i = 200; i < 300; i++) { //SILVER->GOLD
            users.add(User.builder()
                    .totalamt(300_000)
                    .username("user"+i)
                    .build());
        }
        for (int i = 300; i < 400; i++) { //GOLD->VIP
            users.add(User.builder()
                    .totalamt(500_000)
                    .username("user"+i)
                    .build());
        }

        return users;
    }
}
