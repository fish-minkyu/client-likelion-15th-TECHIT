package com.example.api.client;


import com.example.api.dto.ArticleDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.List;

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
