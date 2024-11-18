package practice.batch.flow.transition;

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
public class TransitionConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job batchJob() {
        return new JobBuilder("batchJob", jobRepository)
            .start(batchStep1()).on("FAILED").to(batchStep2())
            .on("FAILED").stop()
            .from(batchStep1()).on("*").to(batchStep3())
            .next(batchStep4())
            .from(batchStep2()).on("*").to(batchStep5())
            .end()
            .build();
    }

    @Bean
    public Step batchStep1() {
        return new StepBuilder("batchStep1", jobRepository)
            .tasklet(((contribution, chunkContext) -> {
                log.info("batchStep1 executed.");
                return RepeatStatus.FINISHED;
            }), transactionManager)
            .build();
    }

    @Bean
    public Step batchStep2() {
        return new StepBuilder("batchStep2", jobRepository)
            .tasklet(((contribution, chunkContext) -> {
                log.info("batchStep2 executed.");
                return RepeatStatus.FINISHED;
            }), transactionManager)
            .build();
    }

    @Bean
    public Step batchStep3() {
        return new StepBuilder("batchStep3", jobRepository)
            .tasklet(((contribution, chunkContext) -> {
                log.info("batchStep3 executed.");
                return RepeatStatus.FINISHED;
            }), transactionManager)
            .build();
    }

    @Bean
    public Step batchStep4() {
        return new StepBuilder("batchStep4", jobRepository)
            .tasklet(((contribution, chunkContext) -> {
                log.info("batchStep4 executed.");
                return RepeatStatus.FINISHED;
            }), transactionManager)
            .build();
    }

    @Bean
    public Step batchStep5() {
        return new StepBuilder("batchStep5", jobRepository)
            .tasklet(((contribution, chunkContext) -> {
                log.info("batchStep5 executed.");
                return RepeatStatus.FINISHED;
            }), transactionManager)
            .build();
    }
}
