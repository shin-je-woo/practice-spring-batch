package practice.batch.application.batch.dto;

import lombok.Builder;

@Builder
public record ApiResponse(
        String status,
        String message
) {
}
