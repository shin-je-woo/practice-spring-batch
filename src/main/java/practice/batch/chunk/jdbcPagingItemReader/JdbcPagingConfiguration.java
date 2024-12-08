package practice.batch.chunk.jdbcPagingItemReader;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
public class JdbcPagingConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private static final int CHUNK_SIZE = 2;

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
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("firstName", "A%");
        return new JdbcPagingItemReaderBuilder<Customer>()
            .name("jdbcPagingItemReader")
            .pageSize(CHUNK_SIZE)
            .dataSource(dataSource)
            .beanRowMapper(Customer.class)
            .queryProvider(queryProvider())
            .parameterValues(parameters)
            .build();
    }

    private PagingQueryProvider queryProvider() {
        final SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("id, first_name, last_name, birth_date");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("first_name like :firstName");

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
    public ItemWriter<Customer> itemWriter() {
        return chunk -> chunk.getItems().forEach(System.out::println);
    }
}
