package practice.batch.job.incrementer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
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
public class ParametersIncrementerConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job batchJob() {
        return new JobBuilder("batchJob", jobRepository)
                .incrementer(new CustomJobParametersIncrementer())
                .start(step1())
                .next(step2())
                .next(step3())
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    final JobParameters jobParameters = contribution.getStepExecution().getJobExecution().getJobParameters();
                    final String customIncrementedParameterValue = jobParameters.getParameters().get("run.dateTime").getValue().toString();
                    log.info("customIncrementedParameterValue : {}", customIncrementedParameterValue);
                    log.info("step1 has executed.");
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
