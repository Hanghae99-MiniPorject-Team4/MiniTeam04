package com.example.advanced.service;

import com.example.advanced.controller.exception.ErrorCode;
import com.example.advanced.controller.handler.CustomException;
import com.example.advanced.controller.request.LoginRequestDto;
import com.example.advanced.controller.request.MemberRequestDto;
import com.example.advanced.controller.response.MemberResponseDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.domain.Member;
import com.example.advanced.domain.Timestamped;
import com.example.advanced.domain.TokenDto;
import com.example.advanced.jwt.TokenProvider;
import com.example.advanced.repository.MemberRepository;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class MemberService extends Timestamped {

  private final MemberRepository memberRepository;

  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;



  @Transactional
  public ResponseDto<?> createMember(MemberRequestDto requestDto) {
    if (null != isPresentMember(requestDto.getNickname())) {
      throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
    }

    if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
      throw new CustomException(ErrorCode.PASSWORDS_NOT_MATCHED);
    }

    Member member = Member.builder()
            .nickname(requestDto.getNickname())
            .password(passwordEncoder.encode(requestDto.getPassword()))
            .build();
    memberRepository.save(member);
    return ResponseDto.success(
            MemberResponseDto.builder()
                    .id(member.getId())
                    .nickname(member.getNickname())
                    .createdAt(member.getCreatedAt())
                    .modifiedAt(member.getModifiedAt())
                    .build()
    );
  }
  @Transactional(readOnly = true)
  public ResponseDto<?> isNickCheck(MemberRequestDto requestDto){
    if (null != isPresentMember(requestDto.getNickname())) {
      return ResponseDto.fail("DUPLICATED_NICKNAME", "중복된 닉네임 입니다.");
    }
    return ResponseDto.success("중복확인 완료");
  }

  @Transactional
  public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
    Member member = isPresentMember(requestDto.getNickname());
    if (null == member) {
      return CustomException.toResponse(new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }

    if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
      return CustomException.toResponse(new CustomException(ErrorCode.NOT_HAVE_PERMISSION));
    }

    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
    tokenToHeaders(tokenDto, response);

    return ResponseDto.success(
            MemberResponseDto.builder()
                    .id(member.getId())
                    .nickname(member.getNickname())
                    .createdAt(member.getCreatedAt())
                    .modifiedAt(member.getModifiedAt())
                    .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> isNickCheck(MemberRequestDto requestDto){
    if (null != isPresentMember(requestDto.getNickname())) {
      return ResponseDto.fail("DUPLICATED_NICKNAME", "중복된 닉네임 입니다.");
    }
    return ResponseDto.success("NICK_CHECK_SUCCESS");
  }


  public ResponseDto<?> logout(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }
    Member member = tokenProvider.getMemberFromAuthentication();
    if (null == member) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "사용자를 찾을 수 없습니다.");
    }

    return tokenProvider.deleteRefreshToken(member);
  }


  public Member isPresentMember(String nickname) {
    Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
    return optionalMember.orElse(null);
  }
  public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
    response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
    response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
    response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
  }

}

