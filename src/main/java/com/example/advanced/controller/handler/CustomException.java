package com.example.advanced.controller.handler;

import com.example.advanced.controller.exception.ErrorCode;
import com.example.advanced.controller.response.ResponseDto;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private ErrorCode error;

    public CustomException(ErrorCode e) {
        super(e.getMessage());
        this.error = e;
    }
    public static ResponseDto<?> toResponse(CustomException e){
        return ResponseDto.fail(e.error.getCode(),e.error.getMessage());
    }
}
