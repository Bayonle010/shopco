package com.shopco.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse {
    private boolean status;
    private int statusCode;
    private String message;
    private Object data;
    private Object metadata;
    private LocalDateTime timeStamp;

    public ApiResponse(boolean status, int statusCode, String message, LocalDateTime timeStamp, Object metadata,  Object data) {
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
        this.timeStamp = timeStamp;
        this.data = data;
        this.metadata = metadata;
    }

}
