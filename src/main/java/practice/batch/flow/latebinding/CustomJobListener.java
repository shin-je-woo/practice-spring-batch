package practice.batch.flow.latebinding;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class CustomJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(final JobExecution jobExecution) {
        jobExecution.getExecutionContext().putString("name", "user1");
    }
}
