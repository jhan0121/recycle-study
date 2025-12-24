package com.recyclestudy.exception.dto;

public record ErrorResponse(String message) {

    public static ErrorResponse from(final String message) {
        return new ErrorResponse(message);
    }
}
