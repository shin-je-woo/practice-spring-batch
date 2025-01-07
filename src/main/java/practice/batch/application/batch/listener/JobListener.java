package practice.batch.application.batch.listener;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
public class JobListener implements JobExecutionListener {

    @Override
    public void beforeJob(@Nonnull JobExecution jobExecution) {
        JobExecutionListener.super.beforeJob(jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        final LocalDateTime startTime = jobExecution.getStartTime();
        final LocalDateTime endTime = jobExecution.getEndTime();

        assert startTime != null;
        Duration duration = Duration.between(startTime, endTime);

        final long millis = duration.toMillis();
        log.info("======== 총 소요 시간 = {} ========", millis);
    }
}
