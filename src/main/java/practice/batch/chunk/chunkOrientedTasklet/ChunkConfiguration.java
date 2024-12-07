package practice.batch.chunk.chunkOrientedTasklet;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ChunkConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job batchJob() {
        return new JobBuilder("batchJob", jobRepository)
            .start(batchStep())
            .build();
    }

    @Bean
    public Step batchStep() {
        return new StepBuilder("batchStep", jobRepository)
            .<String, String>chunk(2, transactionManager)
            .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5", "item6")))
            .processor(item -> {
                Thread.sleep(300);
                System.out.println("item = " + item);
                return "converted " + item;
            })
            .writer(chunk -> {
                final List<? extends String> items = chunk.getItems();
                Thread.sleep(300);
                System.out.println("items = " + items);
            })
            .build();
    }
}
