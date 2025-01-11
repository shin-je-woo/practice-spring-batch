package practice.batch.application.batch.job.file;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import practice.batch.application.batch.domain.Product;
import practice.batch.application.batch.dto.ProductDto;

@Configuration
@RequiredArgsConstructor
public class FileJobConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job fileJob() {
        return new JobBuilder("fileJob", jobRepository)
                .start(fileStep())
                .build();
    }

    @Bean
    public Step fileStep() {
        return new StepBuilder("fileStep", jobRepository)
                .<ProductDto, Product>chunk(10, transactionManager)
                .reader(fileItemReader(null))
                .processor(fileItemProcessor())
                .writer(fileItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<ProductDto> fileItemReader(
            @Value("#{jobParameters['requestDate']}") String requestDate
    ) {
        String classpathFilePath = String.format("application_files/read/product_%s.csv", requestDate);
        return new FlatFileItemReaderBuilder<ProductDto>()
                .name("fileItemReader")
                .resource(new ClassPathResource(classpathFilePath))
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
                .targetType(ProductDto.class)
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("id", "name", "price", "type")
                .build();
    }

    @Bean
    public ItemProcessor<ProductDto, Product> fileItemProcessor() {
        return new FunctionItemProcessor<>(productDto -> Product.of(
                productDto.id(),
                productDto.name(),
                productDto.price(),
                productDto.type())
        );
    }

    @Bean
    public JpaItemWriter<Product> fileItemWriter() {
        return new JpaItemWriterBuilder<Product>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(true)
                .build();
    }
}
