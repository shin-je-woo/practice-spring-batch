package practice.batch.application.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import practice.batch.application.batch.dto.ApiInfo;
import practice.batch.application.batch.dto.ApiRequest;
import practice.batch.application.batch.dto.ApiResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public abstract class AbstractApiService {

    public ApiResponse executeApiRequest(List<? extends ApiRequest> apiRequestList) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .errorHandler(new ResponseErrorHandler() {
                    @Override
                    public boolean hasError(ClientHttpResponse response) throws IOException {
                        return false;
                    }

                    @Override
                    public void handleError(ClientHttpResponse response) throws IOException {

                    }
                })
                .build();

        ApiInfo apiInfo = ApiInfo.builder()
                .apiRequestList(apiRequestList)
                .build();

        return execute(restTemplate, apiInfo);
    }

    public abstract ApiResponse execute(RestTemplate restTemplate, ApiInfo apiInfo);

}
