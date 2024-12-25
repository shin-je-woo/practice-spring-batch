package practice.batch.multiThread.multiThreadedStep;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
public class MultiThreadedConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private static final int CHUNK_SIZE = 100;

    @Bean
    public Job job() {
        return new JobBuilder("job", jobRepository)
            .start(step())
            .incrementer(new RunIdIncrementer())
            .listener(new StopWatchJobListener())
            .build();
    }

    @Bean
    public Step step() {
        return new StepBuilder("step", jobRepository)
            .<Customer, JdbcConvertedCustomer>chunk(CHUNK_SIZE, transactionManager)
            .reader(itemReader())
            .listener(new CustomItemReadListener())
            .processor(itemProcessor())
            .listener(new CustomItemProcessListener())
            .writer(itemWriter())
            .listener(new CustomItemWriteListener())
            .taskExecutor(taskExecutor()) // 이 api만 추가해도 멀티스레드로 동작
            .build();
    }

    @Bean
    public ItemReader<Customer> itemReader() {
        return new JdbcPagingItemReaderBuilder<Customer>()
            .name("jdbcPagingItemReader")
            .pageSize(CHUNK_SIZE)
            .dataSource(dataSource)
            .rowMapper(new CustomerRowMapper())
            .queryProvider(queryProvider())
            .build();
    }

    private PagingQueryProvider queryProvider() {
        final SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("id, first_name, last_name, birth_date");
        queryProvider.setFromClause("from customer");

        final HashMap<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);
        try {
            return queryProvider.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        threadPoolTaskExecutor.setThreadNamePrefix("async-thread");
        return threadPoolTaskExecutor;
    }
}
