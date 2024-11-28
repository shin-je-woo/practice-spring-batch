package practice.batch.flow.latebinding;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class CustomStepListener implements StepExecutionListener {

    @Override
    public void beforeStep(final StepExecution stepExecution) {
        stepExecution.getExecutionContext().putString("name2", "user2");
    }
}
