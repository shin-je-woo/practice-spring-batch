package practice.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(ExecutionContextSerializerConfig.class)
@SpringBootApplication(scanBasePackages = "practice.batch.multiThread.partitioning")
public class PracticeBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(PracticeBatchApplication.class, args);
	}

}
