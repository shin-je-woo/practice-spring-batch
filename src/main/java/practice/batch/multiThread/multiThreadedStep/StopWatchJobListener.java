package practice.batch.multiThread.multiThreadedStep;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
public class StopWatchJobListener implements JobExecutionListener {

    @Override
    public void afterJob(final JobExecution jobExecution) {
        final LocalDateTime startTime = jobExecution.getStartTime();
        final LocalDateTime endTime = jobExecution.getEndTime();

        Duration duration = Duration.between(startTime, endTime);

        final long millis = duration.toMillis();
        log.info("Job execution completed in {} milliseconds", millis);
    }
}
