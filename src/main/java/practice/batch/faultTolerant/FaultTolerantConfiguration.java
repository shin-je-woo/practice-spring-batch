package practice.batch.faultTolerant;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class FaultTolerantConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private static final int CHUNK_SIZE = 5;

    @Bean
    public Job job() {
        return new JobBuilder("job", jobRepository)
            .start(step())
            .incrementer(new RunIdIncrementer())
            .build();
    }

    @Bean
    public Step step() {
        return new StepBuilder("step", jobRepository)
            .<String, String>chunk(CHUNK_SIZE, transactionManager)
            .reader(new ItemReader<>() {
                int count = 0;

                @Override
                public String read() {
                    count++;
                    if (count == 1) {
                        throw new IllegalArgumentException("This exception is skipped");
                    }
                    return count > 3 ? null : "item" + count;
                }
            })
            .processor(item -> {
                throw new IllegalStateException("This exception is retried");
            })
            .writer(chunk -> chunk.forEach(System.out::println))
            .faultTolerant()
            .skip(IllegalArgumentException.class)
            .skipLimit(2)
            .retry(IllegalStateException.class)
            .retryLimit(2)
            .build();
    }
}
