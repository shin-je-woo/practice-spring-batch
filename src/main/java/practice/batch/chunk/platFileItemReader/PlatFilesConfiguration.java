package practice.batch.chunk.platFileItemReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PlatFilesConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job job() {
        return new JobBuilder("job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .next(step2())
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .<Customer, Customer>chunk(5, transactionManager)
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public ItemReader<Customer> itemReader() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        lineMapper.setFieldSetMapper(new CustomerFieldSetMapper());
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer(","));

        return new FlatFileItemReaderBuilder<Customer>()
                .name("platFileIemReader")
                .resource(new ClassPathResource("/customer.csv"))
                .lineMapper(lineMapper)
                .linesToSkip(1) // 첫 번째 row는 skip 한다.
                .build();
    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        return chunk -> chunk.forEach(System.out::println);
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step2 executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }
}
