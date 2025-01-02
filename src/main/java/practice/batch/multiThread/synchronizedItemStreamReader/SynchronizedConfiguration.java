package practice.batch.multiThread.synchronizedItemStreamReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SynchronizedConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private static final int CHUNK_SIZE = 60;

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
                .<Customer, JdbcConvertedCustomer>chunk(CHUNK_SIZE, transactionManager)
                .reader(itemReader())
                .listener(new ItemReadListener<>() {
                    @Override
                    public void afterRead(final Customer item) {
                        log.info("Thread : {}, Read item.getId(): {}", Thread.currentThread().getName(), item.getId());
                    }
                })
                .processor(itemProcessor())
                .writer(itemWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public SynchronizedItemStreamReader<Customer> itemReader() {
        final JdbcCursorItemReader<Customer> jdbcCursorItemReader = new JdbcCursorItemReaderBuilder<Customer>()
                .name("synchronizedItemReader")
                .beanRowMapper(Customer.class)
                .dataSource(dataSource)
                .fetchSize(CHUNK_SIZE)
                .sql("""
                        select id, first_name, last_name, birth_date
                        from customer
                        order by id asc
                        """)
                .build();
        return new SynchronizedItemStreamReaderBuilder<Customer>()
                .delegate(jdbcCursorItemReader)
                .build();
    }

    @Bean
    public ItemProcessor<Customer, JdbcConvertedCustomer> itemProcessor() {
        return item -> new JdbcConvertedCustomer(
                item.getId(),
                item.getFirstName().toUpperCase(),
                item.getLastName().toUpperCase(),
                item.getBirthDate()
        );
    }

    @Bean
    public JdbcBatchItemWriter<JdbcConvertedCustomer> itemWriter() {
        return new JdbcBatchItemWriterBuilder<JdbcConvertedCustomer>()
                .dataSource(dataSource)
                .sql("""
                        insert into jdbc_converted_customer
                        (id, first_name, last_name, birth_date)
                        values
                        (:id, :firstName, :lastName, :birthDate)
                        """)
                .beanMapped()
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(4);
        threadPoolTaskExecutor.setMaxPoolSize(8);
        threadPoolTaskExecutor.setThreadNamePrefix("synchronized-thread");
        return threadPoolTaskExecutor;
    }
}
