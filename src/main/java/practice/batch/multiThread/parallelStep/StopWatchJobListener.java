package practice.batch.multiThread.parallelStep;

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
        log.info("Job이 {} 밀리초만에 완료되었습니다.", millis);
    }
}
