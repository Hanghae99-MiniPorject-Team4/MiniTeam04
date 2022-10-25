package com.example.advanced.controller;

import com.example.advanced.configuration.SwaggerAnnotation;
import com.example.advanced.controller.request.PostRequestDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.service.PostService;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RequiredArgsConstructor
@RestController
public class PostController {

  private final PostService postService;
  private final PostRequestConverter postRequestConverter;
  @SwaggerAnnotation
  @RequestMapping(value = "/api/auth/posts", method = RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
  public ResponseDto<?> createPost(@RequestPart(value = "images", required = false) MultipartFile multipartFile,
                                   @RequestPart(value = "postDto") String requestDto,
                                   HttpServletRequest request) throws IOException {
    PostRequestDto convertedDto = postRequestConverter.convert(requestDto);
    return postService.createPost(convertedDto, multipartFile, request);
  }

  @RequestMapping(value = "/api/posts/{id}", method = RequestMethod.GET)
  public ResponseDto<?> getPost(@PathVariable Long id) {
    return postService.getPost(id);
  }

  @RequestMapping(value = "/api/posts", method = RequestMethod.GET)
  public ResponseDto<?> getAllPosts() {
    return postService.getAllPost();
  }

  @SwaggerAnnotation
  @RequestMapping(value = "/api/auth/posts/{id}", method = RequestMethod.PUT)
  public ResponseDto<?> updatePost(@PathVariable Long id,@RequestPart(value = "postDto") String requestDto,
      HttpServletRequest request,@RequestPart(value = "images", required = false) MultipartFile multipartFile) {
    PostRequestDto convertedDto = postRequestConverter.convert(requestDto);

    return postService.updatePost(id, convertedDto, request, multipartFile);
  }
  @SwaggerAnnotation
  @RequestMapping(value = "/api/auth/posts/{id}", method = RequestMethod.DELETE)
  public ResponseDto<?> deletePost(@PathVariable Long id,
      HttpServletRequest request) {
    return postService.deletePost(id, request);
  }

}
