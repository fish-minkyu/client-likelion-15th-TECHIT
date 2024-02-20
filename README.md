# HTTP Client
- 2024.02.19 `15주차`

<details>
<summary><strong>Page</strong></summary>

- RestTemplate
<div>RestTemplateConfig</div>
<div>ArticleDto</div>
<div>ArticleTemplateClient</div>
<div>ArticleController</div>
<hr>

- WebClient
<div>WebClientConfig</div>
<div>ArticleDto</div>
<div>ArticleWebClient</div>
<div>ArticleController</div>
<hr>

- RestClient
<div>RestClientConfig</div>
<div>ArticleDto</div>
<div>ArticleRestClient</div>
<div>ArticleController</div>
<hr>

- HTTP Interface
<div>ArticleHttpInterface</div>
<div>ArticleDto</div>
<div>ArticleService: implements가 아닌 Bean 주입으로 ArticleHttpInterface 사용</div>
<div>ArticleController</div>
</details>

HTTP 요청을 보내는 Client를 구현해보자.

- [Article - REST project](https://github.com/fish-minkyu/article-likelion-10th-TECHIT)(서버 담당 프로젝트)

: 지난 10주차에서 RESTful 구현한 Article 프로젝트다.  
  http://localhost:8081로 요청을 받도록 Port를 변경하였다.

- `RestTempate`: 가장 오래된 Client 기술, 유지 보수 단계 
- `WebClient`: 근대 방식, 비동기 처리 방식 (사용 추천)
- `RestClient`: 가장 최신 기술, (사용 추천)
- `HTTP Interface`: WebClient와 RestClient 사이에 나온 기술, 간단한 구현 방식이다.


## 스팩

- Spring Boot 3.2.2
- Spring Web
- Lombok

## Key Point

- `Article 서버 - application.yaml`
```yaml
server:
  port: 8081
```

- `RestTemplate`  
<details>
<summary><strong>RestTemplate - Page</strong></summary>

1. RestTemplate 설정  
[RestTemplateConfig](/src/main/java/com/example/api/config/RestTemplateConfig.java)
```java
@Configuration
// 서비스에서 RestTemplate가 필요한 시점에서
// defaultRestTemplate 메서드의 반환 객체인 RestTemplate을 자동으로 주입이 되게끔 만들어준다.
public class RestTemplateConfig {
  @Bean
  // RestTemplateBuilder를 활용해 전체 서비스에서 사용할
  // 기본 설정을 갖춘 RestTemplate을 Bean으로 등록 가능
  public RestTemplate defaultRestTemplate(
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
```

2. RestTemplate - 비즈니스 로직 구현    
[ArticleTemplateClient](/src/main/java/com/example/api/client/ArticleTemplateClient.java)

2-1. create  
```java
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
```
2-2. readAll & readOne
```java
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
```

```java
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
```
2-3. update
```java
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
```
2-4. delete
```java
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
```


3. RestTemplate - Controller (일부)  
[ArticleController](/src/main/java/com/example/api/ArticleController.java)
```java
  @PutMapping("/{id}")
  public ArticleDto update(
    @PathVariable("id") Long id,
    @RequestBody ArticleDto dto
  ) {
    return templateClient.update(id, dto);
  }
```
</details>


- `WebClient`
<details>
<summary><strong>WebClient - Page</strong></summary>

1. WebClient 설정  
[WebClientConfig](/src/main/java/com/example/api/config/WebClientConfig.java)
```java
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
```
2. WebClient - 비즈니스 로직 구현  
[WebClientConfig](/src/main/java/com/example/api/client/ArticleWebClient.java)  
2-1. create
```java
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
```

2-2. readAll & readOne
```java
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
```

```java
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
```

3. WebClient - Controller (일부)  
[ArticleController](/src/main/java/com/example/api/ArticleController.java)
```java
  @GetMapping("/{id}")
  public ArticleDto readOne(
    @PathVariable("id") Long id
  ) {
    // RestTemplate
//    return templateClient.readOne(id);

    // WebClient
    return articleWebClient.readOne(id);
  }
```
</details>

- `RestClient`
<details>
<summary><strong>RestClient - Page</strong></summary>

1. RestClient - 설정  
[RestClientConfig](/src/main/java/com/example/api/config/RestClientConfig.java)
```java
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
```

2. RestClient - 비즈니스 로직  
[ArticleRestClient](/src/main/java/com/example/api/client/ArticleService.java)  
2-1. create
```java
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
```
2-2. readAll
```java
  // readAll
  public List<ArticleDto> readAll() {
    return restClient.get()
      .uri("/articles")
      .retrieve()
      .body(new ParameterizedTypeReference<>() {});
  }
```

2-3. Delete
```java
  // DELETE
  public void delete(Long id) {
    ResponseEntity<Void> responseEntity = restClient.delete()
      .uri("/articles/{id}", id)
      .retrieve()
      .toBodilessEntity();
  }
```

3. RestClient - Controller (일부)  
[ArticleController](/src/main/java/com/example/api/ArticleController.java)
```java
  @GetMapping
  public List<ArticleDto> readAll() {

    // RestTemplate
//    return templateClient.readAll();

    // RestClient
    return restClient.readAll();
  }
```
</details>

- `HTTP Interface`

<details>
<summary><strong>HTTP Interface - Page</strong></summary>

1. HTTP Interface - 정의 및 비즈니스 로직  
[ArticleHttpInterface](/src/main/java/com/example/api/client/ArticleHttpInterface.java)
```java
@HttpExchange("/articles")
public interface ArticleHttpInterface {
  // CRUD를 할것이기 때문에 해당하는 메서드를 다 만든다.

  // @<Method>Exchange  어노테이션
  // : 해당 메서드가 실행될 때, HTTP Request의 메서드와 Path를 결정한다.
  // <Mehotd>Mapping이 요청을 받는 입장이라면,
  // @<Method>Exchange은 요청을 보내고 받는 입장이다.

  // Path, Body, (Query) Parameter를 매개변수로
  // 나타내면 HTTP Request에 포함된다.

  // CREATE
  @PostExchange
  ArticleDto create(
    @RequestBody ArticleDto dto
  );

  // READ all
  @GetExchange
  List<ArticleDto> readAll();

  // READ one
  @GetExchange("/{id}")
  ArticleDto readOne(
    @PathVariable("id") Long id
  );

  // UPDATE
  @PostExchange("/{id}")
  ArticleDto update(
    @PathVariable("id") Long id,
    @RequestBody ArticleDto dto
  );

  // DELETE
  @DeleteExchange("/{id}")
  ArticleDto delete(
    @PathVariable("id") Long id
  );
}
```

2. HTTP Interface 서비스 구현  
[ArticleService](/src/main/java/com/example/api/client/ArticleService.java)  
```java
@Component
public class ArticleService {
  // 사용할 때는 구현체를 만들어 주어야 한다.
  private final ArticleHttpInterface exchange;

  public ArticleService(
    // 실제로 요청을 보내는 역할을 하는
    // HTTP Client 객체가 있어야 한다.
    RestClient restClient
  ) {
    exchange = HttpServiceProxyFactory
      // 내가 사용할 HTTP Client를 사용할 수 있도록 설정
      .builderFor(RestClientAdapter.create(restClient))
      // Proxy를 만드는 Factory를 만든다.
      .build()
      // 해당 RestClient를 바탕으로 Proxy 객체를 만든다.
      .createClient(ArticleHttpInterface.class);
  }

  public ArticleDto create(ArticleDto dto) {
    return exchange.create(dto);
  }

  public ArticleDto readOne(Long id) {
    return exchange.readOne(id);
  }

  public List<ArticleDto> readAll() {
    return exchange.readAll();
  }

  public ArticleDto update(Long id, ArticleDto dto) {
    return exchange.update(id, dto);
  }

  public void delete(Long id) {
    exchange.delete(id);
  }
}
```

3. HTTP Interface - Controller (일부)  
[ArticleController](/src/main/java/com/example/api/ArticleController.java)
```java
  @PostMapping
  public ArticleDto create(
    @RequestBody
    ArticleDto dto
  ) {
    // HTTP Interface
    return service.create(dto);
  }
```

</details>


## GitHub

- 강사님 GitHub
[likelion-backend-8-client](https://github.com/edujeeho0/likelion-backend-8-client)


<details>
<summary><strong>Lecture Page</strong></summary>

# Spring HTTP Clients

Spring에서 제공하는 HTTP Client 들을 활용해서
이전에 만들었던 [Article 서비스](https://github.com/edujeeho0/likelion-backend-8-rest)에 요청을 보내보자.

## Interface 기반 의존성 주입

여기서 사용되는 HTTP Client는 총 4종류이다.
- `RestTemplate`
- `WebClient`
- `HTTP Interface Exchange`
- `RestClient`

이들은 개발 패러다임의 변화와 발전에 맞춰서 등장한 HTTP 요청을 보내는 다양한 방법들을 나타낸다.
새로운 기술을 본래의 프로젝트에 도입하려면 원래의 코드의 수정이 불가피하다.

이때, 각 방식으로 HTTP 요청을 보낸 클래스의 기능을 `interface`로 만들어볼 수 있다.

```java
public interface ArticleClient {
    ArticleDto create(ArticleDto dto);
    ArticleDto readOne(Long id);
    List<ArticleDto> readAll();
    ArticleDto update(Long id, ArticleDto dto);
    void delete(Long id);
}
```

그리고 각 방식으로 HTTP 요청을 보내는 클래스들을, `interface`의 구현체로 만들어줄 수 있다.

```java
// RestClient를 사용하는 구현체
@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleRestClient implements ArticleClient {
    private final RestClient restClient;
    // ...
}


// HTTP Interface를 사용하는 구현체
@Component
public class ArticleService implements ArticleClient {
    // 사용할 때는 구현체를 만들어 주어야 한다.
    private final ArticleHttpInterface exchange;

    public ArticleService(
            // 실제로 요청을 보내는 역할을 하는
            // HTTP Client 객체가 있어야 한다.
            RestClient restClient
    ) {
        exchange = HttpServiceProxyFactory
                // 내가 사용할 HTTP Client를 사용할 수 있도록 설정
                .builderFor(RestClientAdapter.create(restClient))
                // Proxy를 만드는 Factory를 만든다.
                .build()
                // 해당 RestClient를 바탕으로 Proxy 객체를 만든다.
                .createClient(ArticleHttpInterface.class);
    }
    // ...
}
```

이렇게 하면 `ArticleClient`의 기능을 필요로 하는 `ArticleController`에서는,
각 메서드에서는 `ArticleClient`를 사용하는 방식으로 그대로 두고,

```java
@Slf4j
@RestController
@RequestMapping("/articles")
public class ArticleController {
    // ...
    @PostMapping
    public ArticleDto create(
            @RequestBody
            ArticleDto dto
    ) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public ArticleDto readOne(
            @PathVariable("id")
            Long id
    ) {
        return service.readOne(id);
    }
    // ...
}
```

실제로 실행 중에 어떤 구현체를 사용할지는 생성자의 `@Qualifier`, 또는 구현 클래스 주입받기,
`@Primary` 어노테이션 등 다양한 방식으로 결정할 수 있다.

```java
@Slf4j
@RestController
@RequestMapping("/articles")
public class ArticleController {
    // 사용하는 객체는 ArticleClient interface의 구현체로,
    // 어떤 HTTP Client를 사용한들 변하지 않는다.
    private final ArticleClient service;

    public ArticleController(
            // HTTP Interface 방식의 HTTP Client가 주입된다.
            ArticleService articleService
    ) {
        this.service = articleService;
    }
    // ...
}
```

이렇게 `interface`를 이용해 실행중에 어떻게 동작할지를 결정하는 디자인 패턴을 [Strategy Pattern](https://en.wikipedia.org/wiki/Strategy_pattern)
이라 부른다.
</details>