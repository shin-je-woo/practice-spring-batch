package practice.batch.application.batch.processor;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import practice.batch.application.batch.dto.ApiRequest;
import practice.batch.application.batch.dto.ProductDto;

@Slf4j
public class ApiItemProcessor3 implements ItemProcessor<ProductDto, ApiRequest> {

    @Override
    public ApiRequest process(@Nonnull ProductDto item) throws Exception {
        log.info("======== {} ApiItemProcessor3 process ========", Thread.currentThread().getName());
        return ApiRequest.builder()
                .id(item.id())
                .productDto(item)
                .build();
    }
}
