package com.example.advanced.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {


    private String accessToken;

    private Member member;

    @Builder
    public MemberDTO(String accessToken, Member member) {
        this.accessToken = accessToken;
        this.member = member;
    }
}