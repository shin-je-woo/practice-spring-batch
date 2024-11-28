package practice.batch.flow.latebinding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LateBindingConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CustomJobListener customJobListener;
    private final CustomStepListener customStepListener;

    @Bean
    public Job batchJob() {
        return new JobBuilder("batchJob", jobRepository)
            .start(batchStep1(null))
            .next(batchStep2())
            .listener(customJobListener)
            .build();
    }

    @Bean
    @JobScope
    public Step batchStep1(@Value("#{jobParameters['message']}") final String message) {
        log.info("mesage: {}", message);
        return new StepBuilder("batchStep1", jobRepository)
            .tasklet(tasklet1(null), transactionManager)
            .listener(customStepListener)
            .build();
    }

    @Bean
    @JobScope
    public Step batchStep2() {
        return new StepBuilder("batchStep2", jobRepository)
            .tasklet(tasklet2(null), transactionManager)
            .listener(customStepListener)
            .build();
    }

    @Bean
    @StepScope
    public Tasklet tasklet1(@Value("#{jobExecutionContext['name']}") final String name) {
        return (contribution, chunkContext) -> {
            log.info("tasklet1 executed, {}", name);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @StepScope
    public Tasklet tasklet2(@Value("#{stepExecutionContext['name2']}") final String name2) {
        return (contribution, chunkContext) -> {
            log.info("tasklet2 executed, {}", name2);
            return RepeatStatus.FINISHED;
        };
    }
}
