package com.example.advanced.controller;

import com.example.advanced.configuration.SwaggerAnnotation;
import com.example.advanced.controller.request.CommentRequestDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.service.CommentService;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
public class CommentController {

  private final CommentService commentService;

  @SwaggerAnnotation
  @RequestMapping(value = "/api/auth/{postId}/comments", method = RequestMethod.POST)
  public ResponseDto<?> createComment(@PathVariable Long postId, @RequestBody CommentRequestDto requestDto,
                                      HttpServletRequest request) {
    return commentService.createComment(postId, requestDto, request);
  }
  @SwaggerAnnotation
  @RequestMapping(value = "/api/auth/comments/{id}", method = RequestMethod.PUT)
  public ResponseDto<?> updateComment(@PathVariable Long id, @RequestBody CommentRequestDto requestDto,
                                      HttpServletRequest request) {
    return commentService.updateComment(id, requestDto, request);
  }
  @SwaggerAnnotation
  @RequestMapping(value = "/api/auth/comments/{id}", method = RequestMethod.DELETE)
  public ResponseDto<?> deleteComment(@PathVariable Long id,
                                      HttpServletRequest request) {
    return commentService.deleteComment(id, request);
  }
}
