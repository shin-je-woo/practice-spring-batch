package practice.batch.job.incrementer;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CustomJobParametersIncrementer implements JobParametersIncrementer {

    @Override
    public JobParameters getNext(JobParameters parameters) {
        LocalDateTime dateTime = LocalDateTime.now();
        return new JobParametersBuilder().addLocalDateTime("run.dateTime", dateTime).toJobParameters();
    }
}
