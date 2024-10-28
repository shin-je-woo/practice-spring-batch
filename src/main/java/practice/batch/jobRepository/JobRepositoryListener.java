package practice.batch.jobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobRepositoryListener implements JobExecutionListener {

    private final JobRepository jobRepository;

    @Override
    public void beforeJob(final JobExecution jobExecution) {
    }

    @Override
    public void afterJob(final JobExecution jobExecution) {
        final String jobName = jobExecution.getJobInstance().getJobName();
        final JobParameters jobParameters = new JobParametersBuilder()
                .addString("myParamKey", "myParamValue")
                .toJobParameters();
        final JobExecution lastJobExecution = jobRepository.getLastJobExecution(jobName, jobParameters);
        if (lastJobExecution == null) {
            return;
        }
        lastJobExecution.getStepExecutions()
                .forEach(stepExecution -> {
                    final BatchStatus status = stepExecution.getStatus();
                    log.info("BatchStatusName = {}", status.name());
                    log.info("BatchStatusRunning = {}", status.isRunning());
                });
    }
}