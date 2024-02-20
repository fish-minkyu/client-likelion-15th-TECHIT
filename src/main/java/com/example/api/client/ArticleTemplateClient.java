package com.example.api.client;

import com.example.api.config.RestTemplateConfig;
import com.example.api.dto.ArticleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // way 1-1.
    // with uriVariables
    // : String.format() 말고 다른 방법으로 사용
    response = restTemplate.getForObject(
      "/articles/{id}", ArticleDto.class, id
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

    // pageable way1.
    // Object는 아직 타입이 정해지지 않았으므로 사용했다.
    // URL 인자 대체하기, 가변갯수 인자
    Object responsePage = restTemplate.getForObject(
      "/articles/paged?page={page}&limit={limit}",
      Object.class,
      // {} 괄호 순서대로 인자가 들어간다.
      0, // page
      10 // limit
    );
    log.info("response object page: {}", responsePage);

    // pageable way2.
    // 만약 괄호 순서대로 인자가 들어가는게 싫다면?
    // Map을 써볼 수 있다. (단, 중괄호 안의 문자열과 Map의 key는 일치하여야 한다.)
    // URL 인자 대체하기, Map<String, Object>
    Map<String, Object> uriVariables = new HashMap<>();
    uriVariables.put("page", 0);
    uriVariables.put("limit", 5);
    responsePage = restTemplate.getForObject(
      "/articles/paged?page={page}&limit={limit}",
      Object.class,
      uriVariables
    );
    log.info("response object page: {}", responsePage);

    // URI가 너무 길어질 때, StringBuilder와 같은 형식으로
    log.info(UriComponentsBuilder.fromUriString("/articles/paged")
      .queryParam("page", 0)
      .queryParam("limit", 2)
      .toUriString());

    // UriComponentsBuilder는 인코딩을 해준다. 이걸 restTemplate에 넣어주면 한번 더 인코딩이 된다.
    // 그럼 문제 발생
    // Ex) /test?foo=%25%26 -> /test?foo=%2525%2526
    log.info(UriComponentsBuilder.fromUriString("/test")
      .queryParam("foo", "%&")
      .toUriString());

    // UriComponentsBuilder 인코딩을 꺼주고 restTemplate가 인코딩 해주기
    // Ex) /test?foo=%& -> /test?foo=%25%26
    log.info(UriComponentsBuilder.fromUriString("/test")
      .queryParam("foo", "%&")
      .build(false)
      .toUriString());

    // 반환 타입은 List이므로
    // Array를 List로 바꿔주기
    return Arrays.stream(response)
      .toList();
  }

  // PUT
  public ArticleDto update(Long id, ArticleDto dto) {
    // way1. 응답으로 돌아오는 데이터가 없음
    // Put: PUT 요청을 보낸다.
    restTemplate.put(String.format("/articles/%d", id), dto);

    /*
    // 응답으로 돌아온 데이터가 없으므로 인자값을 반환
    return dto;
    */

    // way2. 응답으로 돌아오는 데이터를 받아보기
    // exchange
    ResponseEntity<ArticleDto> responseEntity = restTemplate.exchange(
      String.format("/articles/%d", id),
      HttpMethod.PUT,
      new HttpEntity<>(dto),
      ArticleDto.class
    );
    log.info("status code: {}", responseEntity.getStatusCode());

    return responseEntity.getBody();
  }

  // DELETE
  public void delete(Long id) {
    // way1.
//    restTemplate.delete(String.format("/articles/{id}"), id);

    // way2. 반환 타입과 상태 코드를 보고 싶다면 exchange 사용
    // ResponseEntity<Void>
    // : Response Body가 비어있는 응답
    ResponseEntity<Void> responseEntity = restTemplate.exchange(
      String.format("/articles/%d", id),
      HttpMethod.DELETE,
      null,
      Void.class
    );

    // 서버에 받은 응답 코드 기록
    log.info("status code: {}", responseEntity.getStatusCode());
  }
}


/*
URL 인코딩 이란?
&: 서로 다른 2개의 Parameter를 구분하는 용도로 사용

/search?q=&&page=10
& 표시 자체를 parameter로 포함시키고 싶다면 문제가 생길 수 있다.

/search?q=%26&page=10
그래서, 이런 식으로 % 뒤에다가 유니 코드 또는 아스키 코드를 넣어줌으로써 인코딩하여 표현해준다.

=> restTemplate가 이런 URL 인코딩을 하는 기능을 가지고 있다.
*/