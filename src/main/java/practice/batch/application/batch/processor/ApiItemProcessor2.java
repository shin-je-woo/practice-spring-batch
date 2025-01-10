package practice.batch.application.batch.processor;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import practice.batch.application.batch.dto.ApiRequestDto;
import practice.batch.application.batch.dto.ProductDto;

@Slf4j
public class ApiItemProcessor2 implements ItemProcessor<ProductDto, ApiRequestDto> {

    @Override
    public ApiRequestDto process(@Nonnull ProductDto item) throws Exception {
        log.info("======== {} ApiItemProcessor2 process ========", Thread.currentThread().getName());
        return ApiRequestDto.builder()
                .id(item.id())
                .productDto(item)
                .build();
    }
}
