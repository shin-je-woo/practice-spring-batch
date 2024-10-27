package practice.batch.executionContext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExecutionContextTasklet4 implements Tasklet {

    @Override
    public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {
        log.info("step4 was executed");

        // Step3가 실패하고 재시작했을 경우 ExecutionContext에 있는 값을 공유할 수 있는지?
        final Object name = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("name");
        log.info("name : {}", name);

        return RepeatStatus.FINISHED;
    }
}
