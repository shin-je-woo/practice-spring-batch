package practice.batch.application.batch.step;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.support.builder.ClassifierCompositeItemProcessorBuilder;
import org.springframework.batch.item.support.builder.ClassifierCompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import practice.batch.application.batch.dto.ApiRequest;
import practice.batch.application.batch.dto.ProductDto;
import practice.batch.application.batch.partition.ProductPartitioner;
import practice.batch.application.batch.processor.ApiItemProcessor1;
import practice.batch.application.batch.processor.ApiItemProcessor2;
import practice.batch.application.batch.processor.ApiItemProcessor3;
import practice.batch.application.batch.rowmapper.ProductDtoRowMapper;
import practice.batch.application.batch.utils.QueryGenerator;
import practice.batch.application.batch.writer.ApiItemWriter1;
import practice.batch.application.batch.writer.ApiItemWriter2;
import practice.batch.application.batch.writer.ApiItemWriter3;
import practice.batch.application.service.ApiService1;
import practice.batch.application.service.ApiService2;
import practice.batch.application.service.ApiService3;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ApiStepConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private static final int CHUNK_SIZE = 10;

    private final ApiService1 apiService1;
    private final ApiService2 apiService2;
    private final ApiService3 apiService3;

    @Bean
    public Step apiManagerStep() {
        return new StepBuilder("apiManagerStep", jobRepository)
                .partitioner(apiWorkerStep().getName(), partitioner())
                .step(apiWorkerStep())
                .gridSize(3)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step apiWorkerStep() {
        return new StepBuilder("apiWorkerStep", jobRepository)
                .<ProductDto, ApiRequest>chunk(CHUNK_SIZE, transactionManager)
                .reader(itemReader(null))
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3);
        taskExecutor.setMaxPoolSize(6);
        taskExecutor.setThreadNamePrefix("api-thread-");
        return taskExecutor;
    }

    @Bean
    public Partitioner partitioner() {
        return new ProductPartitioner(dataSource);
    }

    @Bean
    @StepScope
    public ItemReader<ProductDto> itemReader(
            @Value("#{stepExecutionContext['productType']}") String productType
    ) {
        return new JdbcPagingItemReaderBuilder<ProductDto>()
                .name("jdbcPagingItemReader")
                .pageSize(CHUNK_SIZE)
                .dataSource(dataSource)
                .rowMapper(new ProductDtoRowMapper())
                .queryProvider(queryProvider())
                .parameterValues(QueryGenerator.getParameterForQuery("type", productType))
                .build();
    }

    private PagingQueryProvider queryProvider() {
        final SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("id, name, price, type");
        queryProvider.setFromClause("from product");
        queryProvider.setWhereClause("type = :type");

        final HashMap<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.DESCENDING);
        queryProvider.setSortKeys(sortKeys);
        try {
            return queryProvider.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 상품 타입에 따라 Processor를 결정한다.
     */
    @Bean
    @StepScope
    public ItemProcessor<ProductDto, ApiRequest> itemProcessor() {
        Map<String, ItemProcessor<ProductDto, ApiRequest>> processorMap = new HashMap<>();
        processorMap.put("1", new ApiItemProcessor1());
        processorMap.put("2", new ApiItemProcessor2());
        processorMap.put("3", new ApiItemProcessor3());

        return new ClassifierCompositeItemProcessorBuilder<ProductDto, ApiRequest>()
                .classifier((Classifier<ProductDto, ItemProcessor<?, ? extends ApiRequest>>)
                        classifiable -> processorMap.get(classifiable.type()))
                .build();
    }

    /**
     * 상품 타입에 따라 Writer를 결정한다.
     */
    @Bean
    @StepScope
    public ItemWriter<ApiRequest> itemWriter() {
        Map<String, ItemWriter<ApiRequest>> writerMap = new HashMap<>();
        writerMap.put("1", new ApiItemWriter1(apiService1));
        writerMap.put("2", new ApiItemWriter2(apiService2));
        writerMap.put("3", new ApiItemWriter3(apiService3));

        return new ClassifierCompositeItemWriterBuilder<ApiRequest>()
                .classifier((Classifier<ApiRequest, ItemWriter<? super ApiRequest>>) classifiable ->
                        writerMap.get(classifiable.productDto().type()))
                .build();
    }
}
