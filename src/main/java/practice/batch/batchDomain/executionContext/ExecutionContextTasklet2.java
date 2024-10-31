package practice.batch.batchDomain.executionContext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExecutionContextTasklet2 implements Tasklet {

    @Override
    public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {
        log.info("step2 was executed");

        final ExecutionContext jobExecutionContext = contribution.getStepExecution().getJobExecution().getExecutionContext();
        final ExecutionContext stepExecutionContext = contribution.getStepExecution().getExecutionContext();

        log.info("jobName : {}", jobExecutionContext.get("jobName"));
        log.info("stepName : {}", stepExecutionContext.get("stepName"));

        final String stepName = chunkContext.getStepContext().getStepName();
        if (stepExecutionContext.get("stepName") == null) {
            stepExecutionContext.put("stepName", stepName);
        }

        return RepeatStatus.FINISHED;
    }
}
