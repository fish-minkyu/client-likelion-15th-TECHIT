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


## 스팩

- Spring Boot 3.2.2
- Spring Web
- Lombok

## Key Point


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