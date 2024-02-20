package com.example.api.client;

import com.example.api.dto.ArticleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
// RestTemplate 객체를 활용하여
// RestTemplate가 가지고 있는 method를 호출했을 때, HTTP 요청이 보내지는 방법이다.
public class ArticleTemplateClient {
  private final RestTemplate restTemplate;

  // POST 요청
  public ArticleDto create(ArticleDto dto) {
    // postForObject: 객체를 받기 위해 POST 요청을 한다.
    // (응답 Body만 반환이 됨)
    ArticleDto response = restTemplate.postForObject(
      // 요청 url
      "/articles",
      // Request Body
      // (필요없을 때는 null)
      dto,
      // Response Body의 자료형
      ArticleDto.class
    );
    log.info("response: {}", response);

    // postForEntity: ResponseEntity를 받기 위해 POST 요청을 한다.
    // (응답 Header, Status, Body 등등이 반환이 됨)
    ResponseEntity<ArticleDto> responseEntity = restTemplate.postForEntity(
      // 요청 url
      "/articles",
      // Request Body
      // (필요없을 때는 null)
      dto,
      // Response Body의 자료형
      ArticleDto.class
    );
    log.info("responseEntity: {}", responseEntity);
    log.info("status code: {}", responseEntity.getStatusCode());
    log.info("headers: {}", responseEntity.getHeaders());
    response = responseEntity.getBody();

    // Request Body
    return response;
  }

  // GET 요청

  // readOne
  public ArticleDto readOne(Long id) {
    // way1.
    // getForObject: 객체를 받기 위해 GET 요청을 한다.
    ArticleDto response = restTemplate.getForObject(
      String.format("/articles/%d", id), ArticleDto.class
    );
    log.info("response: {}", response);

    // way2.
    // getForEntity: ResponseEntity를 받기 위해 GET 요청을 한다.
    ResponseEntity<ArticleDto> responseEntity = restTemplate.getForEntity(
      String.format("/articles/%d", id), ArticleDto.class
    );
    log.info("responseEntity: {}", responseEntity);
    log.info("status code: {}", responseEntity.getStatusCode());

    // way3.
    // getForObject - Object
    Object responseObject = restTemplate.getForObject(
      String.format("/articles/%d", id), Object.class
    );
    log.info("response object: {}", responseObject.getClass());

    return response;
  }

  // readAll
  public List<ArticleDto> readAll() {
    // way 1.
    // getForObject
    ArticleDto[] response = restTemplate.getForObject(
      "/articles",
      // 타입 파라미터를 ArticleDto로 받으면 List의 내용물을 꺼내기가 힘드므로 배열로 넣어준다.
      ArticleDto[].class
    );
    log.info("response type: {}", response.getClass());

    // way2.
    // getForEntity
    ResponseEntity<ArticleDto[]> responseEntity = restTemplate.getForEntity(
      "/articles", ArticleDto[].class
    );

    log.info("responseEntity: {}", responseEntity);
    log.info("status code: {}", responseEntity.getStatusCode());

    // way3. 배열을 쓰고 싶지 않고 바로 List를 사용하고 싶을 때
    // exchange: 일반적인 상황에서 HTTP 요청의 모든 것(메서드, 헤더, 바디 등등...)을
    // 묘사하여 요청하기 위한 메서드
    // + ParameterizedTypeReference<T>를 사용하면 List로 반환된다.
    ResponseEntity<List<ArticleDto>> responseListEntity = restTemplate.exchange(
      "/articles",
      HttpMethod.GET,
      // Body를 명시적으로 null로 전달을 해줘야 한다.
      null,
      new ParameterizedTypeReference<>() {}
    );
    log.info("response parameterized: {}", responseListEntity.getBody().getClass());

//    return responseListEntity.getBody();

    // way4. Object으로
    // getForObject - Object
    Object responseObject = restTemplate.getForObject(
      "/articles", Object.class
    );
    log.info("response object: {}", responseObject.getClass());


    // 반환 타입은 List이므로
    // Array를 List로 바꿔주기
    return Arrays.stream(response)
      .toList();
  }
}
