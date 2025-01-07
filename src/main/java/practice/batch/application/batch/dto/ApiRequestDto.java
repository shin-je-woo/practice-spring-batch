package practice.batch.application.batch.dto;

import lombok.Builder;

@Builder
public record ApiRequestDto(
        Long id,
        ProductDto productDto
) {
}
