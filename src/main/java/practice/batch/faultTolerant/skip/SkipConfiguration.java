package practice.batch.faultTolerant.skip;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SkipConfiguration {
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
            .reader(new ItemReader<>() {
                int count = 0;

                @Override
                public Integer read() {
                    count++;
                    if (count == 3) {
                        throw new CustomSkippableException("skip exception occurred");
                    }
                    System.out.println("ItemReader : " + count);
                    return count > 20 ? null : count;
                }
            })
            .processor(itemProcessor())
            .writer(itemWriter())
            .faultTolerant()
            .skip(CustomSkippableException.class)
            .noSkip(CustomNonSkippableException.class)
            .skipLimit(4)
            .build();
    }

    @Bean
    public ItemProcessor<Integer, String> itemProcessor() {
        return new SkipItemProcessor();
    }

    @Bean
    public ItemWriter<String> itemWriter() {
        return new SkipItemWriter();
    }
}
