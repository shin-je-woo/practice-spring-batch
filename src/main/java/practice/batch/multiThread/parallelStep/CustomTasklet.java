package practice.batch.multiThread.parallelStep;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class CustomTasklet implements Tasklet {
    private long sum = 0; // 공유 데이터에 대한 쓰기작업은 동기화 필요
    private final Lock lock = new ReentrantLock();

    @Override
    public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {
        final boolean available = lock.tryLock(5, TimeUnit.SECONDS);
        if (!available) throw new RuntimeException("failed to lock  ");
        try {
            for (int i = 0; i < 1_000_000_000; i++) {
                sum++;
            }
            log.info("{} 은 {} 쓰레드에 의해 실행되었습니다. SUM = {}",
                chunkContext.getStepContext().getStepName(),
                Thread.currentThread().getName(),
                sum
            );
        } finally {
            lock.unlock();
        }
        return RepeatStatus.FINISHED;
    }
}
