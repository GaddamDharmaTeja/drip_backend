package com.dripgrow.api.dto;

import java.time.Instant;

public class ApiError {

    private Instant timestamp = Instant.now();
    private String message;

    public ApiError() {
    }

    public ApiError(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
