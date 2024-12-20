package practice.batch.chunk.jpaPagingItemReader;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import practice.batch.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest
@Import({JpaPagingConfiguration.class})
class JpaPagingConfigurationTest extends IntegrationTest {
    @Autowired
    Job job;
    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void test() throws Exception {
        // given
        final JobParameters jobParameters = new JobParametersBuilder()
            .addString("name", "user1")
            .toJobParameters();
        jobLauncherTestUtils.setJob(job);

        // when
        final JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    }
}