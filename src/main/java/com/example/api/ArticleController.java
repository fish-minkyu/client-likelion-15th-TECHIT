package com.example.api;

import com.example.api.client.ArticleRestClient;
import com.example.api.client.ArticleService;
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
  private final ArticleRestClient restClient;
  private final ArticleService service;

  @PostMapping
  public ArticleDto create(
    @RequestBody
    ArticleDto dto
  ) {
    // RestTemplate
//    return templateClient.create(dto);

    // WebClient
//    return articleWebClient.create(dto);

    // RestClient
//    return restClient.create(dto);

    // HTTP Interface
    return service.create(dto);
  }

  @GetMapping("/{id}")
  public ArticleDto readOne(
    @PathVariable("id") Long id
  ) {
    // RestTemplate
//    return templateClient.readOne(id);

    // WebClient
    return articleWebClient.readOne(id);
  }

  @GetMapping
  public List<ArticleDto> readAll() {

    // RestTemplate
//    return templateClient.readAll();

    // RestClient
    return restClient.readAll();
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
