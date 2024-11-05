package practice.batch.job.preventRestart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PreventRestartConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job batchJob() {
        return new JobBuilder("batchJob", jobRepository)
                .start(step1())
                .next(step2())
                .next(step3())
                .preventRestart()
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step1 has executed.");
                    if (Objects.equals(contribution.getStepExecution().getJobExecution().getJobParameters().getString("name"), "user1")) {
                        throw new RuntimeException("일부러 내는 오류다~");
                    }
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step2 has executed.");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step step3() {
        return new StepBuilder("step3", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step3 has executed.");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }
}
