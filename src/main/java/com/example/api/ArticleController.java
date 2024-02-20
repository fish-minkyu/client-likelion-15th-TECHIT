package com.example.api;

import com.example.api.client.ArticleTemplateClient;
import com.example.api.dto.ArticleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {
  private final ArticleTemplateClient templateClient;

  @PostMapping
  public ArticleDto create(
    @RequestBody
    ArticleDto dto
  ) {
    return templateClient.create(dto);
  }

  @GetMapping("/{id}")
  public ArticleDto readOne(
    @PathVariable("id") Long id
  ) {
    return templateClient.readOne(id);
  }

  @GetMapping
  public List<ArticleDto> readAll() {
    return templateClient.readAll();
  }
}
