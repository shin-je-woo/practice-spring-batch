package practice.batch.application.batch.dto;

import lombok.Builder;

@Builder
public record ApiRequest(
        Long id,
        ProductDto productDto
) {
}
