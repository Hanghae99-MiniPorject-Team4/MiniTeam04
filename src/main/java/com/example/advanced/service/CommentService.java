package com.example.advanced.service;

import com.example.advanced.controller.exception.ErrorCode;
import com.example.advanced.controller.handler.CustomException;
import com.example.advanced.controller.request.CommentRequestDto;
import com.example.advanced.controller.response.CommentResponseDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.domain.Comment;
import com.example.advanced.domain.Member;
import com.example.advanced.domain.Post;
import com.example.advanced.jwt.TokenProvider;
import com.example.advanced.repository.CommentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;

  private final TokenProvider tokenProvider;

  private final PostService postService;

  @Transactional
  public ResponseDto<?> createComment(Long postId, CommentRequestDto requestDto, HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return CustomException.toResponse(new CustomException(ErrorCode.NOT_FOUND_POST));
//      throw new CustomException(ErrorCode.JWT_REFRESH_TOKEN_EXPIRED);
    }

    Post post = postService.isPresentPost(postId);
    if (null == post) {
      return CustomException.toResponse(new CustomException(ErrorCode.NOT_FOUND_POST));
    }

    Comment comment = Comment.builder()
        .member(member)
        .post(post)
        .content(requestDto.getContent())
        .build();
    commentRepository.save(comment);

    return ResponseDto.success(
        CommentResponseDto.builder()
            .id(comment.getId())
            .author(comment.getMember().getNickname())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllCommentsByPost(Long postId) {
    Post post = postService.isPresentPost(postId);
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
    return ResponseDto.success(commentResponseDtoList);
  }


  @Transactional
  public ResponseDto<?> updateComment(Long id, CommentRequestDto requestDto, HttpServletRequest request) {
    Member member =  validateMember(request);
    if (null == member) {
      return CustomException.toResponse(new CustomException(ErrorCode.JWT_REFRESH_TOKEN_EXPIRED));
    }


    Comment comment = isPresentComment(id);
    if (null == comment) {
      return CustomException.toResponse(new CustomException(ErrorCode.NOT_FOUND_COMMENT));
    }

    if (comment.validateMember(member)) {
      return CustomException.toResponse(new CustomException(ErrorCode.NOT_HAVE_PERMISSION));
    }

    comment.update(requestDto);
    return ResponseDto.success(
        CommentResponseDto.builder()
            .id(comment.getId())
            .author(comment.getMember().getNickname())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .build()
    );
  }

  @Transactional
  public ResponseDto<?> deleteComment(Long id, HttpServletRequest request) {
    Member member =  validateMember(request);
    if (null == member) {
      return CustomException.toResponse(new CustomException(ErrorCode.JWT_REFRESH_TOKEN_EXPIRED));
    }

    Comment comment = isPresentComment(id);
    if (null == comment) {
      return CustomException.toResponse(new CustomException(ErrorCode.NOT_FOUND_COMMENT));
    }

    if (comment.validateMember(member)) {
      return CustomException.toResponse(new CustomException(ErrorCode.NOT_HAVE_PERMISSION));
    }


    commentRepository.delete(comment);
    return ResponseDto.success("success");
  }



  @Transactional(readOnly = true)
  public Comment isPresentComment(Long id) {
    Optional<Comment> optionalComment = commentRepository.findById(id);
    return optionalComment.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }
}