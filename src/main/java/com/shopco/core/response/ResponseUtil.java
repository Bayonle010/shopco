package com.shopco.core.response;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class ResponseUtil {
    public static  ApiResponse  success (int statusCode, String message, Object data , Object metadata){
        return new ApiResponse(true, statusCode, message,null, data, metadata,  LocalDateTime.now(ZoneId.of("Africa/Lagos")));
    }

    public static ApiResponse error (int statusCode, String message , String details,  Object data){
        return new ApiResponse(false, statusCode, message, details, data, null,  LocalDateTime.now(ZoneId.of("Africa/Lagos")));
    }
}
