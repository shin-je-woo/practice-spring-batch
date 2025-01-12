package practice.batch.application.batch.dto;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequest {
    private Long id;
    private ProductDto productDto;
    @Setter
    private ApiResponse apiResponse;
}
