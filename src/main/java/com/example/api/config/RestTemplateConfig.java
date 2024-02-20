package com.example.api.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
// 서비스에서 RestTemplate가 필요한 시점에서
// defaultRestTemplate 메서드의 반환 객체인 RestTemplate을 자동으로 주입이 되게끔 만들어준다.
public class RestTemplateConfig {
  @Bean
  // RestTemplateBuilder를 활용해 전체 서비스에서 사용할
  // 기본 설정을 갖춘 RestTemplate을 Bean으로 등록 가능
  public RestTemplate 능defaultRestTemplate(
    // Bean 객체로 주입을 받을 수 있는 RestTemplateBuilder
    RestTemplateBuilder templateBuilder
  ) {
    // 그냥 새로 생성해서 사용할 수도 있음
    // RestTemplate restTemplate = new RestTemplate();

    // restTemplate을 templateBuilder로 초기화
    // 이렇게 함으로 http://localhost:8081로 URI를 통일할 수 있다.
    return templateBuilder
      .rootUri("http://localhost:8081")
      .build();
  }
}
