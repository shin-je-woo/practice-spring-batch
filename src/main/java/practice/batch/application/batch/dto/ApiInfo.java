package practice.batch.application.batch.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ApiInfo(
        String url,
        List<? extends ApiRequest> apiRequestList
) {
}
