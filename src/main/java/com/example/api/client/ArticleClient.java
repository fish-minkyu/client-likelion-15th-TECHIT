package com.example.api.client;

import com.example.api.dto.ArticleDto;

import java.util.List;

public interface ArticleClient {
  ArticleDto create(ArticleDto dto);
  ArticleDto readOne(Long id);
  List<ArticleDto> readAll();
  ArticleDto update(Long id, ArticleDto dto);
  void delete(Long id);
}
