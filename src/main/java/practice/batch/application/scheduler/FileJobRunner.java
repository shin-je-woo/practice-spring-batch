package practice.batch.application.scheduler;

import lombok.RequiredArgsConstructor;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileJobRunner extends JobRunner {
    private final Scheduler scheduler;

    @Override
    protected void runInternal() throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(FileScheduledJob.class, "fileJob", "batch");
        Trigger trigger = buildTrigger("0/50 * * * * ?");
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
