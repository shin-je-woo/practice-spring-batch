package practice.batch.application.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiScheduledJob extends QuartzJobBean {
    private final Job apiJob;
    private final JobExplorer jobExplorer;
    private final JobLauncher jobLauncher;

    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        final JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
                .getNextJobParameters(apiJob)
                .toJobParameters();
        jobLauncher.run(apiJob, jobParameters);
    }
}
