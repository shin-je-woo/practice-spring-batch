package practice.batch.multiThread.partitioning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PartitioningConfiguration {
    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private static final int CHUNK_SIZE = 100;

    @Bean
    public Job job() {
        return new JobBuilder("job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(managerStep())
                .build();
    }

    @Bean
    public Step managerStep() {
        return new StepBuilder("managerStep", jobRepository)
                .partitioner(workerStep().getName(), partitioner())
                .step(workerStep())
                .gridSize(4)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public Step workerStep() {
        return new StepBuilder("workerStep", jobRepository)
                .<Customer, JdbcConvertedCustomer>chunk(CHUNK_SIZE, transactionManager)
                .reader(itemReader(null, null))
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Partitioner partitioner() {
        ColumnRangePartitioner columnRangePartitioner = new ColumnRangePartitioner();

        columnRangePartitioner.setColumn("id");
        columnRangePartitioner.setDataSource(dataSource);
        columnRangePartitioner.setTable("customer");

        return columnRangePartitioner;
    }

    @Bean
    @StepScope
    public ItemReader<Customer> itemReader(
            @Value("#{stepExecutionContext['minValue']}") final Long minValue,
            @Value("#{stepExecutionContext['maxValue']}") final Long maxValue
    ) {
        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("jdbcPagingItemReader")
                .fetchSize(1000)
                .dataSource(dataSource)
                .rowMapper(new CustomerRowMapper())
                .queryProvider(queryProvider(minValue, maxValue))
                .build();
    }

    private PagingQueryProvider queryProvider(final Long minValue, final Long maxValue) {
        log.info("{} 쓰레드 아이템 리더의 minValue: {}, maxValue: {}",
                Thread.currentThread().getName(),
                minValue,
                maxValue
        );

        final SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);

        queryProvider.setSelectClause("id, first_name, last_name, birth_date");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("id >= " + minValue + " and id <= " + maxValue);

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
    @StepScope
    public ItemProcessor<Customer, JdbcConvertedCustomer> itemProcessor() {
        return item -> new JdbcConvertedCustomer(
                item.getId(),
                item.getFirstName().toUpperCase(),
                item.getLastName().toUpperCase(),
                item.getBirthDate()
        );
    }

    @Bean
    @StepScope
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
}
