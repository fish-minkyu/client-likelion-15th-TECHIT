package com.example.api.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Configuration
public class RestClientConfig {
  @Bean
  // RestClient: Builder를 활용해 전체 서비스에서 사용할
  // 기본 설정을 갖춘 WebClient Bean으로 등록 가능
  public RestClient defaultRestClient() {
//    RestClient restClient = RestClient.create();
    return RestClient.builder()
      .baseUrl("http://localhost:8081")
      .defaultHeader("test0", "foo")
      .defaultRequest(request ->
        request.header("test1", "bar"))
      .defaultStatusHandler(
        HttpStatusCode::isError, ((request, response) -> {
         throw new ResponseStatusException(response.getStatusCode());}
        )
      )
      .build();
  }
}
