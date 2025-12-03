package com.shopco.payment.service.impl;

import com.shopco.core.config.RestClientConfig;
import com.shopco.payment.dto.response.AccessTokenResponse;
import com.shopco.payment.service.PaymentService;
import com.shopco.payment.util.Base64FormatConversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class PaymentServiceImpl implements PaymentService {

    private final static Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final Base64FormatConversion base64FormatConversion;
    private final RestClient paymentRestClient;

    public PaymentServiceImpl(Base64FormatConversion base64FormatConversion,
                              @Qualifier("monnifyRestClient") RestClient paymentRestClient) {
        this.base64FormatConversion = base64FormatConversion;
        this.paymentRestClient = paymentRestClient;
    }

    @Override
    public String getAccessTokenFromMonnify() {
        try {
            AccessTokenResponse response = paymentRestClient.post()
                    .uri(builder-> builder.path("/api/v1/auth/login")
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + base64FormatConversion.returnEncodeCredentials())
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .body(AccessTokenResponse.class);

            if (response != null && response.requestSuccessful()) {
                logger.info("access token is : {}", response.responseBody().accessToken());
                return response.responseBody().accessToken();
            } else {
                // Handle other errors, you can throw an exception, log, or return a default value
                throw new RuntimeException("Failed to retrieve access token: " +
                        (response != null ? response.responseMessage() : "Internal Server"));
            }
        } catch (Exception e) {
            // Handle exceptions if any
            logger.error("Error while getting access token", e);
            return  ("Error while getting access token ");
        }
    }
}
