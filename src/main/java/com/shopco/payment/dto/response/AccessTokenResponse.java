package com.shopco.payment.dto.response;

public record AccessTokenResponse(
         boolean requestSuccessful,
         String responseMessage,
         String responseCode,
         ResponseBody responseBody
) {
    public record ResponseBody(
            String accessToken,
            int expiresIn
    ){
    }
}
