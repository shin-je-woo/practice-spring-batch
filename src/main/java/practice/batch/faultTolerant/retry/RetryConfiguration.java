package practice.batch.faultTolerant.retry;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;

@Configuration
@RequiredArgsConstructor
public class RetryConfiguration {
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
            .<Integer, String>chunk(CHUNK_SIZE, transactionManager)
            .reader(itemReader())
            .processor(itemProcessor())
            .writer(chunk -> chunk.forEach(System.out::println))
            .faultTolerant()
            .skip(CustomRetryableException.class)
            .skipLimit(2)
            .retry(CustomRetryableException.class)
            .retryLimit(2)
            .backOffPolicy(backOffPolicy())
            .build();
    }

    @Bean
    public ListItemReader<Integer> itemReader() {
        final ArrayList<Integer> items = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            items.add(i);
        }
        return new ListItemReader<>(items);
    }

    @Bean
    public ItemProcessor<Integer, String> itemProcessor() {
        return new RetryItemProcessor();
    }

    @Bean
    public BackOffPolicy backOffPolicy() {
        final FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000);
        return fixedBackOffPolicy;
    }
}
