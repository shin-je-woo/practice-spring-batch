package practice.batch.chunk.jpaPagingItemReader;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

;import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
public class JpaPagingConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
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
            .<User, User>chunk(CHUNK_SIZE, transactionManager)
            .reader(itemReader())
            .writer(itemWriter())
            .build();
    }

    @Bean
    public ItemReader<User> itemReader() {
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("ageMin", 30);
        parameters.put("ageMax", 35);
        return new JpaPagingItemReaderBuilder<User>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(CHUNK_SIZE)
            .queryString("""
                select u
                from User u
                left join fetch u.address
                where u.age between :ageMin and :ageMax
                """)
            .parameterValues(parameters)
            .build();
    }

    @Bean
    public ItemWriter<User> itemWriter() {
        return chunk -> chunk.getItems().forEach(System.out::println);
    }
}
