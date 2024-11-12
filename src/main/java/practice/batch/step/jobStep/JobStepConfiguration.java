package practice.batch.step.jobStep;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JobStepConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobLauncher jobLauncher;

    @Bean
    public Job parentJob() {
        return new JobBuilder("parentJob", jobRepository)
            .start(jobStep())
            .next(step2())
            .build();
    }

    @Bean
    public Step jobStep() {
        return new StepBuilder("jobStep", jobRepository)
            .job(childJob())
            .launcher(jobLauncher)
            .parametersExtractor(jobParametersExtractor())
            .listener(new StepExecutionListener() {
                @Override
                public void beforeStep(final StepExecution stepExecution) {
                    stepExecution.getExecutionContext().put("name", "user1");
                }
            })
            .build();
    }

    public JobParametersExtractor jobParametersExtractor() {
        final DefaultJobParametersExtractor jobParametersExtractor = new DefaultJobParametersExtractor();
        jobParametersExtractor.setKeys(new String[]{"name"});
        return jobParametersExtractor;
    }


    @Bean
    public Job childJob() {
        return new JobBuilder("childJob", jobRepository)
            .start(step1())
            .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
            .tasklet(((contribution, chunkContext) -> {
//                throw new RuntimeException("일부러 내는 오류입니다.");
                return RepeatStatus.FINISHED;
            }), transactionManager)
            .build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
            .tasklet(((contribution, chunkContext) -> {
                throw new RuntimeException("일부러 내는 오류입니다.");
//                return RepeatStatus.FINISHED;
            }), transactionManager)
            .build();
    }
}
