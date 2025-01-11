package practice.batch.application.batch.writer;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import practice.batch.application.batch.dto.ApiRequest;
import practice.batch.application.batch.dto.ApiResponse;
import practice.batch.application.service.AbstractApiService;

@Slf4j
@RequiredArgsConstructor
public class ApiItemWriter3 implements ItemWriter<ApiRequest> {
    private final AbstractApiService apiService;

    @Override
    public void write(@Nonnull Chunk<? extends ApiRequest> chunk) throws Exception {
        ApiResponse apiResponse = apiService.executeApiRequest(chunk.getItems());
        log.info("ApiItemWriter3의 response = {}", apiResponse.toString());
    }
}
