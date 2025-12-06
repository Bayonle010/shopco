package com.shopco.payment.service.impl;

import com.shopco.cart.entity.Cart;
import com.shopco.cart.service.CartService;
import com.shopco.core.response.ApiResponse;
import com.shopco.core.response.ResponseUtil;
import com.shopco.payment.builder.PaymentBuilder;
import com.shopco.payment.dto.request.PaymentInitializationRequest;
import com.shopco.payment.dto.response.AccessTokenResponse;
import com.shopco.payment.dto.response.PaymentInitializationResponse;
import com.shopco.payment.service.PaymentService;
import com.shopco.payment.util.Base64FormatConversion;
import com.shopco.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Service
public class MonnifyPaymentServiceImpl implements PaymentService {


    @Value("${monnify.contractCode}")
    private String contractCode;

    @Value("${monnify.redirectUri}")
    private String redirectUri;

    private final static Logger logger = LoggerFactory.getLogger(MonnifyPaymentServiceImpl.class);

    private final Base64FormatConversion base64FormatConversion;
    private final RestClient paymentRestClient;
    private final UserService userService;
    private final CartService cartService;

    public MonnifyPaymentServiceImpl(Base64FormatConversion base64FormatConversion,
                                     @Qualifier("monnifyRestClient") RestClient paymentRestClient, UserService userService, CartService cartService) {
        this.base64FormatConversion = base64FormatConversion;
        this.paymentRestClient = paymentRestClient;
        this.userService = userService;
        this.cartService = cartService;
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

    @Override
    public ResponseEntity<ApiResponse> initializePayment(UUID cartId, Authentication authentication) {

        Cart cart = cartService.findCartById(cartId);

        cartService.validateCartForAuthenticatedUser(cart, authentication);

        PaymentInitializationRequest request = PaymentBuilder.buildPaymentRequest(cart, contractCode, redirectUri);
        logger.info("request to get checkout Url -->{}", request);

        try {
            PaymentInitializationResponse response = paymentRestClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/api/v1/merchant/transactions/init-transaction").build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessTokenFromMonnify())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(PaymentInitializationResponse.class);

            return ResponseEntity.ok(ResponseUtil.success(0, "Payment initialization successful", response, null));

        }catch (RestClientResponseException e) {
            // Handle errors from RestClient (specific response errors)
            logger.error("Error initializing payment", e);
            return ResponseEntity.status(e.getStatusCode())
                    .body(ResponseUtil.error(99, "Error initializing payment: " + e.getResponseBodyAsString(), "", null));
        } catch (Exception e) {
            // Handle unexpected errors
            logger.error("Unexpected error during payment initialization", e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
