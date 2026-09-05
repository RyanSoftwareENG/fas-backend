package com.fas.exception;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;


@Builder
public class ErrorResponse {


    private int status;

    private String error;

    private String message;

    private String path;

    private LocalDateTime timestamp;

    private Map<String,String> validationErrors;



    public int getStatus() {
        return status;
    }


    public String getError() {
        return error;
    }


    public String getMessage() {
        return message;
    }


    public String getPath() {
        return path;
    }


    public LocalDateTime getTimestamp() {
        return timestamp;
    }


    public Map<String,String> getValidationErrors() {
        return validationErrors;
    }
}