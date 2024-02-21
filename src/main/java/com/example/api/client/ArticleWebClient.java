package com.example.api.client;

import com.example.api.dto.ArticleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleWebClient implements ArticleClient {
  private final WebClient webClient;

  // POST
  public ArticleDto create(ArticleDto dto) {
    // Way1.
    // WebClient는 HTTP 요청을 Build 한다고 생각해보자.
    ArticleDto response = webClient
      // POST 요청이다.
      .post()
      // 경로 설정
      .uri("/articles")
      // Request Body 설정
      .bodyValue(dto)
      // 여기부터 응답을 어떻게 처리할지
      .retrieve()
      // Mono 응답을 받는다. (반응형 웹이 지원되지 않고 있다면 bodyToMono를 쓰면 된다.)
      .bodyToMono(ArticleDto.class)
      // 동기식으로 처리한다.
      .block();

    log.info("response: {}", response);

    // Way2.
    ResponseEntity<ArticleDto> responseEntity =  webClient
      // POST 요청이다.
      .post()
      // 경로 설정
      .uri("/articles")
      // Request Body 설정
      .bodyValue(dto)
      // 여기부터 응답을 어떻게 처리할지
      .retrieve()
      // ResponseEntity가 담긴 Mono를 받는다.
      .toEntity(ArticleDto.class)
      // 동기식으로 처리한다.
      .block();

    log.info("responseEntity: {}", responseEntity);

    return response;
  }

  // GET
  // readOne
  public ArticleDto readOne(Long id) {
    // Way1.
    ArticleDto response = webClient
      .get()
      .uri("/articles/{id}", id)
      .retrieve()
      .bodyToMono(ArticleDto.class)
      .block();

    log.info("response: {}", response);

    // Way2.
    Map<String, Object> uriVariables = new HashMap<>();
    uriVariables.put("id", id);
    response = webClient
      .get()
      .uri("/articles/{id}", uriVariables)
      .retrieve()
      .bodyToMono(ArticleDto.class)
      .block();

    log.info("response: {}", response);

    return response;
  }

  // readAll
  public List<ArticleDto> readAll() {
    // ParameterizedTypeReference
    List<ArticleDto> response = webClient.get()
      .uri("/articles")
      .retrieve()
      .bodyToMono(
        new ParameterizedTypeReference<List<ArticleDto>>() {})
      .block();

    return response;
  }

  // update
  public ArticleDto update(Long id, ArticleDto dto) {
    ArticleDto response = webClient.put()
      .uri("/articles")
      .bodyValue(dto)
      .retrieve()
      .bodyToMono(ArticleDto.class)
      .block();

    return response;
  }

  // delete
  public void delete(Long id) {
    Map<String, Object> uriVariables = new HashMap<>();
    uriVariables.put("id", id);
    ResponseEntity<?> responseEntity = webClient.delete()
      .uri("/articles/{id}", uriVariables)
      .retrieve()
      .toBodilessEntity()
      .block();
    log.info("status code: {}", responseEntity.getStatusCode());
  }
}
