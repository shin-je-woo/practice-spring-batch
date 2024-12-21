package practice.batch.operation;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class JobOperationController {
    private final JobExplorer jobExplorer;
    private final JobRegistry jobRegistry;
    private final JobOperator jobOperator;

    @SneakyThrows
    @PostMapping("/batch/start")
    public String start(@RequestBody MyJobParameter myJobParameter) {
        for (String jobName : jobRegistry.getJobNames()) {
            final Job job = jobRegistry.getJob(jobName);
            System.out.println("jobName: " + job.getName());

            final Properties properties = new Properties();
            properties.put("id", myJobParameter.id() + ",java.lang.Long"); // JobOperator에서 파라미터 설정 주의!
            jobOperator.start(job.getName(), properties);
        }
        return "batch started";
    }

    @SneakyThrows
    @PostMapping("/batch/stop")
    public String stop() {
        for (String jobName : jobRegistry.getJobNames()) {
            final Job job = jobRegistry.getJob(jobName);
            final Set<JobExecution> runningJobExecutions = jobExplorer.findRunningJobExecutions(job.getName());

            for (JobExecution jobExecution : runningJobExecutions) {
                jobOperator.stop(jobExecution.getId()); // 현재 실행중인 Step까지는 실행하고 종료
                System.out.println("jobName: " + job.getName() + " stopped!!!!!");
            }
        }
        return "batch stopped";
    }

    @SneakyThrows
    @PostMapping("/batch/restart")
    public String restart() {
        for (String jobName : jobRegistry.getJobNames()) {
            final Job job = jobRegistry.getJob(jobName);

            final JobInstance lastJobInstance = jobExplorer.getLastJobInstance(job.getName());
            final JobExecution lastJobExecution = jobExplorer.getLastJobExecution(lastJobInstance);

            if(lastJobExecution.getStatus() != BatchStatus.COMPLETED) {
                jobOperator.restart(lastJobExecution.getId());
            }
        }
        return "batch restarted";
    }
}
