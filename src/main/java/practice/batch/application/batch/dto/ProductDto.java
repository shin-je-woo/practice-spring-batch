package practice.batch.application.batch.dto;

import lombok.Builder;

@Builder
public record ProductDto(
        Long id,
        String name,
        int price,
        String type
) {
    public ProductDto() {
        this(0L, "defaultName", 0, "defaultType");
    }
}