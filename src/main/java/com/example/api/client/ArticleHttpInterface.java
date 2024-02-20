package com.example.api.client;

import com.example.api.dto.ArticleDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

// 요청이 어떤 식으로 보내져야 하는지가 정의된 인터페이스
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
