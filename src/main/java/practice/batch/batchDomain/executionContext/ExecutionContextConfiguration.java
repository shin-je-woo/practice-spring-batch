package practice.batch.batchDomain.executionContext;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ExecutionContextConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final Tasklet executionContextTasklet1;
    private final Tasklet executionContextTasklet2;
    private final Tasklet executionContextTasklet3;
    private final Tasklet executionContextTasklet4;

    @Bean
    public Job batchJob() {
        return new JobBuilder("executionContextJob", jobRepository)
                .start(step1())
                .next(step2())
                .next(step3())
                .next(step4())
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet(executionContextTasklet1, transactionManager)
                .build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .tasklet(executionContextTasklet2, transactionManager)
                .build();
    }

    @Bean
    public Step step3() {
        return new StepBuilder("step3", jobRepository)
                .tasklet(executionContextTasklet3, transactionManager)
                .build();
    }

    @Bean
    public Step step4() {
        return new StepBuilder("step4", jobRepository)
                .tasklet(executionContextTasklet4, transactionManager)
                .build();
    }
}
