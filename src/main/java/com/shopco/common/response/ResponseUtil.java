package com.shopco.common.response;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class ResponseUtil {
    public static  ApiResponse  success (int statusCode, String message, Object data , Object metadata){
        return new ApiResponse(true, statusCode, message, LocalDateTime.now(ZoneId.of("Africa/Lagos")), data, metadata);
    }

    public static ApiResponse error (int statusCode, String message , Object data){
        return new ApiResponse(false, statusCode, message, LocalDateTime.now(ZoneId.of("Africa/Lagos")), data, null);
    }
}
