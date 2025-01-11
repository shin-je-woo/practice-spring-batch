package practice.batch.application.scheduler;

import org.quartz.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public abstract class JobRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        runInternal();
    }

    protected abstract void runInternal() throws SchedulerException;

    protected JobDetail buildJobDetail(Class<? extends Job> job, String jobName, String jobGroup) {
        return JobBuilder.newJob(job)
                .withIdentity(jobName, jobGroup)
                .build();
    }

    protected Trigger buildTrigger(String scheduleExp) {
        return TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(scheduleExp))
                .build();
    }
}
