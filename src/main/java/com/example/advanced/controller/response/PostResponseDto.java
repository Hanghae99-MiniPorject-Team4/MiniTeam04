package com.example.advanced.controller.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.advanced.domain.Category;
import com.example.advanced.domain.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
  private Long id;
  private String title;
  private String content;
  private String imgUrl;
  private String author;
  private Category category;
  private List<CommentResponseDto> comments;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
