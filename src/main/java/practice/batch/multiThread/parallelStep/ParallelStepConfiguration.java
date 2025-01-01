package practice.batch.multiThread.parallelStep;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ParallelStepConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job job() {
        return new JobBuilder("job", jobRepository)
            .incrementer(new RunIdIncrementer())
            .listener(new StopWatchJobListener())
            .start(flow1())
            .split(taskExecutor()).add(flow2())
            .end()
            .build();
    }

    @Bean
    public Flow flow1() {
        final TaskletStep step1 = new StepBuilder("step1", jobRepository)
            .tasklet(tasklet(), transactionManager)
            .build();

        return new FlowBuilder<Flow>("flow1")
            .start(step1)
            .build();
    }

    @Bean
    public Flow flow2() {
        final TaskletStep step2 = new StepBuilder("step2", jobRepository)
            .tasklet(tasklet(), transactionManager)
            .build();

        final TaskletStep step3 = new StepBuilder("step3", jobRepository)
            .tasklet(tasklet(), transactionManager)
            .build();

        return new FlowBuilder<Flow>("flow2")
            .start(step2)
            .next(step3)
            .build();
    }

    @Bean
    public Tasklet tasklet() {
        return new CustomTasklet();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(2);
        threadPoolTaskExecutor.setMaxPoolSize(4);
        threadPoolTaskExecutor.setThreadNamePrefix("async-thread");
        return threadPoolTaskExecutor;
    }
}
