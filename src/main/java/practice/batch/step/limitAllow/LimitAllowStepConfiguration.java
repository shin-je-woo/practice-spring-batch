package practice.batch.step.limitAllow;

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

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LimitAllowStepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job batchJob() {
        return new JobBuilder("batchJob1", jobRepository)
                .start(batchStep1())
                .next(batchStep2())
                .build();
    }

    @Bean
    public Step batchStep1() {
        return new StepBuilder("batchStep1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("Batch step 1 started");
                    log.info("stepContribution = {} , chunkContext = {}", contribution, chunkContext);
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step batchStep2() {
        return new StepBuilder("batchStep2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("Batch step 2 started");
                    log.info("stepContribution = {} , chunkContext = {}", contribution, chunkContext);
                    throw new RuntimeException("일부러 내는 오류");
//                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .startLimit(3)
                .build();
    }
}
