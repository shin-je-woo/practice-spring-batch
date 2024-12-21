package practice.batch.operation;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JobOperationConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job job() {
        return new JobBuilder("job", jobRepository)
            .start(step1())
            .next(step2())
            .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("step1 executed");
                Thread.sleep(10_000);
                System.out.println("step1 finished");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("step2 executed");
//                Thread.sleep(10_000);
//                System.out.println("step2 finished");
//                return RepeatStatus.FINISHED;
                throw new RuntimeException("step2 실패!");
            }, transactionManager)
            .build();
    }
}
