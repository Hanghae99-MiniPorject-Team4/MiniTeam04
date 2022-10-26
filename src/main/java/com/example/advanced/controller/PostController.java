package com.example.advanced.controller;

import com.example.advanced.configuration.SwaggerAnnotation;
import com.example.advanced.controller.request.PostRequestDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.service.FileService;
import com.example.advanced.service.FileUpdateService;
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

  private final FileUpdateService fileService;

  @SwaggerAnnotation
  @RequestMapping(value = "/api/auth/posts", method = RequestMethod.POST,consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = "application/json")
  public ResponseDto<?> createPost(@RequestBody PostRequestDto requestDto,
                                   HttpServletRequest request) {
    return postService.createPost(requestDto, request);
  }

  @RequestMapping(value = "/api/auth/images", method = RequestMethod.POST,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseDto<?> createImage(@RequestParam(value = "images", required = false) MultipartFile images,
                                   HttpServletRequest request) {
    fileService.createImage(images, request);
    return ResponseDto.success("Image upload success!");
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
  @RequestMapping(value = "/api/auth/posts/{id}", method = RequestMethod.PUT,consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = "application/json")
  public ResponseDto<?> updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto,
                                   HttpServletRequest request) {
    return postService.updatePost(id, requestDto, request);
  }

  @RequestMapping(value = "/api/auth/images", method = RequestMethod.PUT,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseDto<?> updateImage(HttpServletRequest request, @RequestParam(value = "images", required = false) MultipartFile images) {
    fileService.update(images, request);
    return ResponseDto.success("Image update success!");
  }
  @SwaggerAnnotation
  @RequestMapping(value = "/api/auth/posts/{id}", method = RequestMethod.DELETE)
  public ResponseDto<?> deletePost(@PathVariable Long id,
      HttpServletRequest request) {
    return postService.deletePost(id, request);
  }

}
