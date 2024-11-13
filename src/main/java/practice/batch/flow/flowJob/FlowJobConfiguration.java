package practice.batch.flow.flowJob;

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
public class FlowJobConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job batchJob() {
        return new JobBuilder("batchJob", jobRepository)
            .start(batchStep1())
            .on("COMPLETED").to(batchStep2())
            .from(batchStep1())
            .on("FAILED").to(batchStep3())
            .end()
            .build();
    }

    @Bean
    public Step batchStep1() {
        return new StepBuilder("batchStep1", jobRepository)
            .tasklet(((contribution, chunkContext) -> {
                log.info("Batch step 1 started");
//                throw new RuntimeException("Batch step 1 failed");
                return RepeatStatus.FINISHED;
            }), transactionManager)
            .build();
    }

    @Bean
    public Step batchStep2() {
        return new StepBuilder("batchStep2", jobRepository)
            .tasklet(((contribution, chunkContext) -> {
                log.info("Batch step 2 started");
                return RepeatStatus.FINISHED;
            }), transactionManager)
            .build();
    }

    @Bean
    public Step batchStep3() {
        return new StepBuilder("batchStep3", jobRepository)
            .tasklet(((contribution, chunkContext) -> {
                log.info("Batch step 3 started");
                return RepeatStatus.FINISHED;
            }), transactionManager)
            .build();
    }
}
