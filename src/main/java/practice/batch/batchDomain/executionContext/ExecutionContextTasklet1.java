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
public class ExecutionContextTasklet1 implements Tasklet {

    @Override
    public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {
        log.info("step1 was executed");

        final ExecutionContext jobExecutionContext = contribution.getStepExecution().getJobExecution().getExecutionContext();
        final ExecutionContext stepExecutionContext = contribution.getStepExecution().getExecutionContext();

        final String jobName = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getJobName();
        final String stepName = chunkContext.getStepContext().getStepName();

        if (jobExecutionContext.get("jobName") == null) {
            jobExecutionContext.put("jobName", jobName);
        }

        if (stepExecutionContext.get("stepName") == null) {
            stepExecutionContext.put("stepName", stepName);
        }

        log.info("jobName : {}", jobExecutionContext.get("jobName"));
        log.info("stepName : {}", stepExecutionContext.get("stepName"));

        return RepeatStatus.FINISHED;
    }
}
