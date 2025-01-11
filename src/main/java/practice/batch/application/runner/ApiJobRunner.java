package practice.batch.application.runner;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiJobRunner implements ApplicationRunner {
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final Job apiJob;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
                .getNextJobParameters(apiJob)
                .toJobParameters();
        jobLauncher.run(apiJob, jobParameters);
    }
}
