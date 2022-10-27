package com.example.advanced.service;

import com.example.advanced.controller.exception.ErrorCode;
import com.example.advanced.controller.handler.CustomException;
import com.example.advanced.controller.request.PostRequestDto;
import com.example.advanced.controller.response.CommentResponseDto;
import com.example.advanced.controller.response.PostListResponseDto;
import com.example.advanced.controller.response.PostResponseDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.domain.*;
import com.example.advanced.jwt.TokenProvider;
import com.example.advanced.repository.CommentRepository;
import com.example.advanced.repository.FileRepository;
import com.example.advanced.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService extends Timestamped {

  private final PostRepository postRepository;

  private final CommentRepository commentRepository;

  private final FileRepository fileRepository;

  private final TokenProvider tokenProvider;



  @Transactional
  public ResponseDto<?> createPost(PostRequestDto requestDto, HttpServletRequest request)  {
    Member member = validateMember(request);
    if (null == member) {
      throw new CustomException(ErrorCode.JWT_REFRESH_TOKEN_EXPIRED);
    }

    Post post = Post.builder()
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .category(requestDto.getCategory())
            .member(member)
            .build();
    postRepository.save(post);

    return ResponseDto.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .imgUrl(post.getImages())
                    .author(post.getMember().getNickname())
                    .category(post.getCategory())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getPost(Long id) {
    Post post = isPresentPost(id);
    if (null == post) {
      return CustomException.toResponse(new CustomException(ErrorCode.NOT_FOUND_POST));

    }

    List<Comment> commentList = commentRepository.findAllByPost(post);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    for (Comment comment : commentList) {
      commentResponseDtoList.add(
              CommentResponseDto.builder()
                      .id(comment.getId())
                      .author(comment.getMember().getNickname())
                      .content(comment.getContent())
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
                      .build()
      );
    }
    Files image = fileRepository.findById(id).orElse(null);
    return ResponseDto.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .author(post.getMember().getNickname())
                    .imgUrl(image.getUrl())
                    .category(post.getCategory())
                    .comments(commentResponseDtoList)
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllPost() {
    List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();
    List<PostListResponseDto> postListResponseDtoList = new ArrayList<>();
    for (Post post : postList) {
      Files image = fileRepository.findById(post.getId()).orElse(null);
      int comments = commentRepository.countAllByPost(post);
      postListResponseDtoList.add(
              PostListResponseDto.builder()
                      .id(post.getId())
                      .title(post.getTitle())
                      .content(post.getContent())
                      .author(post.getMember().getNickname())
                      .imgUrl(image.getUrl())
                      .commentsNum(comments)
                      .createdAt(post.getCreatedAt())
                      .modifiedAt(post.getModifiedAt())
                      .build()
      );
    }

    return ResponseDto.success(postListResponseDtoList);
  }

  @Transactional
  public ResponseDto<?> updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return CustomException.toResponse(new CustomException(ErrorCode.JWT_REFRESH_TOKEN_EXPIRED));
    }

    Post post = isPresentPost(id);
    if (null == post) {
      return CustomException.toResponse(new CustomException(ErrorCode.NOT_FOUND_POST));
    }

    if (post.validateMember(member)) {
      return CustomException.toResponse(new CustomException(ErrorCode.NOT_HAVE_PERMISSION));
    }

    post.update(requestDto);
    return ResponseDto.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .imgUrl(post.getImages())
                    .author(post.getMember().getNickname())
                    .category(post.getCategory())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
    );
  }

  @Transactional
  public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return CustomException.toResponse(new CustomException(ErrorCode.JWT_REFRESH_TOKEN_EXPIRED));
    }

    Post post = isPresentPost(id);
    if (null == post) {
      return CustomException.toResponse(new CustomException(ErrorCode.NOT_FOUND_POST));
    }

    if (post.validateMember(member)) {
      return CustomException.toResponse(new CustomException(ErrorCode.NOT_HAVE_PERMISSION));
    }

    postRepository.delete(post);
     return ResponseDto.success("delete success");
  }


  @Transactional(readOnly = true)
  public Post isPresentPost(Long id) {
    Optional<Post> optionalPost = postRepository.findById(id);
    return optionalPost.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }

}

