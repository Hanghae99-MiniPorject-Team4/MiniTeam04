package com.example.advanced.domain;

import lombok.Getter;

import java.util.Arrays;
@Getter
public enum Category {
    서울("서울"),
    경기도("경기도"),
    경상도("경상도"),
    충청도("충청도"),
    전라도("전라도"),
    제주도("제주도"),
    타지역("타지역");

    private  String value;

    Category(String value) {
        this.value = value;
    }

    public static Category fromCode(String dbData){
        return Arrays.stream(Category.values())
                .filter(v -> v.getValue().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("포스트 카테고리에 %s가 존재하지 않습니다.", dbData)));
    }
}
