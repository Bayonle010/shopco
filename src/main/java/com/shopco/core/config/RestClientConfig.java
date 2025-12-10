package com.shopco.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Configuration
public class RestClientConfig {
    private final static Logger log = LoggerFactory.getLogger(RestClientConfig.class);


    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(
            @Value("${http.connect-timeout-ms:5000}") int connectTimeoutMs,
            @Value("${http.response-timeout-ms:15000}") int responseTimeoutMs
    ) {
        HttpClient jdk = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                .version(HttpClient.Version.HTTP_2)
                .build();

        var jdkFactory = new JdkClientHttpRequestFactory(jdk);
        jdkFactory.setReadTimeout(Duration.ofMillis(responseTimeoutMs));

        return new BufferingClientHttpRequestFactory(jdkFactory);
    }

    @Bean
    public RestClient.Builder restClientBuilder(ClientHttpRequestFactory factory) {
        return RestClient.builder()
                .requestFactory(factory)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                //  Predicate over HttpStatusCode, not (req, resp)
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        //  Error handler gets (HttpRequest, ClientHttpResponse)
                        (request, response) -> {
                            var status = response.getStatusCode();   // HttpStatusCode
                            var body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                            throw new RuntimeException("Upstream error " + status.value() + ": " + body);
                        }
                );
    }


    @Bean
    @Qualifier("monnifyRestClient")
    public RestClient verificationRestClient(@Value("${monnify.uri}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}