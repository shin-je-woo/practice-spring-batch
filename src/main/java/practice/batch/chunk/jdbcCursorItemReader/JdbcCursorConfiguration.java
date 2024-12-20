package practice.batch.chunk.jdbcCursorItemReader;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JdbcCursorConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private static final int CHUNK_SIZE = 10;

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
            .<Customer, Customer>chunk(CHUNK_SIZE, transactionManager)
            .reader(itemReader())
            .writer(itemWriter())
            .build();
    }

    @Bean
    public ItemReader<Customer> itemReader() {
        return new JdbcCursorItemReaderBuilder<Customer>()
            .name("customerItemReader")
            .beanRowMapper(Customer.class)
            .fetchSize(CHUNK_SIZE)
            .sql("""
                select id, first_name, last_name, birth_date
                from customer
                where first_name like ?
                order by last_name, first_name
                """)
            .queryArguments("A%")
            .dataSource(dataSource)
            .build();
    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        return chunk -> chunk.getItems().forEach(System.out::println);
    }
}
