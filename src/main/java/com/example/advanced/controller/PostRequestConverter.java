package com.example.advanced.controller;

import com.example.advanced.controller.request.PostRequestDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;


@Component
public class PostRequestConverter implements Converter<String, PostRequestDto> {

    private final ObjectMapper objectMapper;
    public PostRequestConverter(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    public PostRequestDto convert (String value){
        return objectMapper.readValue(value, new TypeReference<>() {
        });
    }


}
