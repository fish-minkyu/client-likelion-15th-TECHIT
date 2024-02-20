package com.example.api;

import com.example.api.client.ArticleTemplateClient;
import com.example.api.client.ArticleWebClient;
import com.example.api.dto.ArticleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {
  private final ArticleTemplateClient templateClient;
  private final ArticleWebClient articleWebClient;

  @PostMapping
  public ArticleDto create(
    @RequestBody
    ArticleDto dto
  ) {
    return articleWebClient.create(dto);
  }

  @GetMapping("/{id}")
  public ArticleDto readOne(
    @PathVariable("id") Long id
  ) {
    return articleWebClient.readOne(id);
  }

  @GetMapping
  public List<ArticleDto> readAll() {
    return templateClient.readAll();
  }

  @PutMapping("/{id}")
  public ArticleDto update(
    @PathVariable("id") Long id,
    @RequestBody ArticleDto dto
  ) {
    return templateClient.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
    @PathVariable("id") Long id
  ) {
    templateClient.delete(id);
  }
}
