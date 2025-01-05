package practice.batch.chunk.flatFileItemWriter;

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
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FlatFilesConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job job() {
        return new JobBuilder("job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .<Customer, Customer>chunk(3, transactionManager)
                .reader(itemReader())
                .writer(delimitedItemWriter())
                .build();
    }

    @Bean
    public ItemReader<Customer> itemReader() {
        List<Customer> customers = Arrays.asList(
                new Customer(1, "customer1", 30),
                new Customer(2, "customer2", 31),
                new Customer(3, "customer3", 32)
        );
        return new ListItemReader<>(customers);
    }

    @Bean
    public ItemWriter<Customer> delimitedItemWriter() {
        return new FlatFileItemWriterBuilder<Customer>()
                .name("delimitedFlatFileWriter")
                .resource(new FileSystemResource("/Users/jewoo/IdeaProjects/practice-spring-batch/src/main/resources/delimited_customer_write.csv"))
                .headerCallback(writer -> writer.write("아이디,이름,나이"))
                .append(true) // 기존 파일이 존재하면 이어붙인다.
                .shouldDeleteIfEmpty(true) // 쓰기 작업할 데이터가 없으면 파일을 삭제한다.
                .delimited()
                .delimiter(",")
                .names("id", "name", "age")
                .build();
    }

//    @Bean
    public ItemWriter<Customer> formattedItemWriter() {
        return new FlatFileItemWriterBuilder<Customer>()
                .name("formattedItemWriter")
                .resource(new FileSystemResource("/Users/jewoo/IdeaProjects/practice-spring-batch/src/main/resources/formatted_customer_write.txt"))
                .formatted()
                .format("%d %s %d")
                .names("id", "name", "age")
                .build();
    }
}