package com.example.api.client;

import com.example.api.dto.ArticleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleRestClient {
  private final RestClient restClient;

  // POST
  // 동기식으로 작동한다.
  public ArticleDto create(ArticleDto dto) {
    ArticleDto response = restClient
      // POST 요청이다.
      .post()
      // 경로 설정
      .uri("/articles")
      // Body 설정
      .body(dto)
      // 여기부터 응답을 어떻게 처리할지
      .retrieve()
      // 그냥 DTO가 반환된다.
      .body(ArticleDto.class);
    log.info("response: {}", response);

    ResponseEntity<ArticleDto> responseEntity = restClient
      .post()
      .uri("/articles")
      .body(dto)
      .retrieve()
      // 그냥 ResponseEntity가 반환된다.
      .toEntity(ArticleDto.class);
    log.info("responseEntity: {}", responseEntity);

    return response;
  }

  // GET
  // readAll
  public List<ArticleDto> readAll() {
    return restClient.get()
      .uri("/articles")
      .retrieve()
      .body(new ParameterizedTypeReference<>() {});
  }

  // DELETE
  public void delete(Long id) {
    ResponseEntity<Void> responseEntity = restClient.delete()
      .uri("/articles/{id}", id)
      .retrieve()
      .toBodilessEntity();
  }
}
