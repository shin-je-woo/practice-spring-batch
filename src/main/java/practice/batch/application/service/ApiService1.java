package practice.batch.application.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import practice.batch.application.batch.dto.ApiInfo;
import practice.batch.application.batch.dto.ApiResponse;

@Service
public class ApiService1 extends AbstractApiService {

    @Override
    public ApiResponse execute(RestTemplate restTemplate, ApiInfo apiInfo) {
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8081/api/product/1", apiInfo, String.class);
        HttpStatusCode statusCode = response.getStatusCode();

        return ApiResponse.builder()
                .status(statusCode.toString())
                .message(response.getBody())
                .build();
    }
}
