package com.example.api;

import com.example.api.client.ArticleClient;
import com.example.api.client.ArticleService;
import com.example.api.client.ArticleTemplateClient;
import com.example.api.dto.ArticleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {
  private final ArticleClient service;

  // ArticleClient 인터페이스 생성자 주입
  // 구현 로직에 상관없이 공통된 기능을 인터페이스로 묶어서 implements를 받고
  // 생성자 주입은 구현한 4가지 방법(RestTemplate, WebClient, Http Interface, Restclient) 중에
  // 아무거나 넣어서 동작할 수 있게 할 수 있다.
  // (이렇게 함으로서, 결합성을 줄일 수 있다.)
  public TestController(
    // RestTemplate
    ArticleTemplateClient articleTemplateClient

    // HTTP Interface
//    ArticleService articleService
  ) {
    // RestTemplate
    this.service = articleTemplateClient;

    // HTTP Interface
//    this.service = articleService;
  }

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

  @GetMapping
  public List<ArticleDto> readAll() {
    return service.readAll();
  }

  @PutMapping("{id}")
  public ArticleDto update(
    @PathVariable("id")
    Long id,
    @RequestBody
    ArticleDto dto
  ) {
    return service.update(id, dto);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
    @PathVariable("id")
    Long id
  ) {
    service.delete(id);
  }
}
