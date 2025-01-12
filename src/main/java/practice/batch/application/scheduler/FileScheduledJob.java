package practice.batch.application.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileScheduledJob extends QuartzJobBean {
    private final Job fileJob;
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;

    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        final JobParameters jobParameters = new JobParametersBuilder()
                .addString("requestDate", getRandomParam(List.of("20250105", "20250106")))
                .toJobParameters();

        JobInstance jobInstance = jobExplorer.getJobInstance(fileJob.getName(), jobParameters);
        if (jobInstance != null) {
            boolean isCompletedJob = jobExplorer.getJobExecutions(jobInstance).stream()
                    .map(JobExecution::getStatus)
                    .anyMatch(batchStatus -> batchStatus == BatchStatus.COMPLETED);
            if (isCompletedJob) {
                log.info("fileJob이 이미 수행되어 재실행하지 않습니다.");
                return;
            }
        }

        jobLauncher.run(fileJob, jobParameters);
    }

    public static String getRandomParam(List<String> params) {
        Random random = new Random();
        int index = random.nextInt(params.size());
        return params.get(index);
    }
}
