package com.shopco.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse {
    private boolean status;
    private int statusCode;
    private String message;
    private String details;
    private Object data;
    private Object metadata;
    private LocalDateTime timeStamp;

    public ApiResponse(boolean status, int statusCode, String message,String details,  LocalDateTime timeStamp,   Object data, Object metadata) {
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
        this.details = details;
        this.timeStamp = timeStamp;
        this.data = data;
        this.metadata = metadata;
    }

}
