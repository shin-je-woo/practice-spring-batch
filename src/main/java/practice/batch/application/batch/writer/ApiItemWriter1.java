package practice.batch.application.batch.writer;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;
import practice.batch.application.batch.dto.ApiRequest;
import practice.batch.application.batch.dto.ApiResponse;
import practice.batch.application.service.AbstractApiService;

@Slf4j
@RequiredArgsConstructor
public class ApiItemWriter1 extends FlatFileItemWriter<ApiRequest> {
    private final AbstractApiService apiService;

    @Override
    public void write(@Nonnull Chunk<? extends ApiRequest> chunk) throws Exception {
        ApiResponse apiResponse = apiService.executeApiRequest(chunk.getItems());
        log.info("ApiItemWriter1의 response = {}", apiResponse.toString());

        chunk.forEach(item -> item.setApiResponse(apiResponse));

        super.setResource(new FileSystemResource("/Users/jewoo/IdeaProjects/practice-spring-batch/src/main/resources/application_files/write/product1.csv"));
        super.open(new ExecutionContext());
        super.setLineAggregator(new DelimitedLineAggregator<>());
        super.setAppendAllowed(true);
        super.write(chunk);
    }
}
