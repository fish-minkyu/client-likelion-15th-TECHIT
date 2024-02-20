package com.example.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Configuration
public class WebClientConfig {
  @Bean
  // WebClient: Builder를 활용해 전체 서비스에서 사용할
  // 기본 설정을 갖춘 WebClient Bean으로 등록 가능
  public WebClient defaultWebClient() {
    // 그냥 새로 생성해서 사용할 수도 있음
//   WebClient webClient = WebClient.create();

    // 여러 설정을 포함해서 Builder 형태로 만들고 싶다면?
    // RestTemplate에 비해 선언형 (함수형) 구조를 가진다.
    return WebClient.builder()
      .baseUrl("http://localhost:8081")
      // 토큰 기반 인증을 사용하는 API를 사용한다면?
      .defaultHeader("test", "foo")
      // 요청이 보내지기 전에 요청에다가 추가적인 헤더들을 지정해서 함수를 넣어줄 수 있다.
      .defaultRequest(request ->
        request.header("test", "bar"))
      .defaultStatusHandler(
        // 들어온 StatusCode가 400번대, 500번대인지?
        HttpStatusCode::isError,
        response -> {
          throw new ResponseStatusException(response.statusCode());
        }
      )
      .build();

  }
}
