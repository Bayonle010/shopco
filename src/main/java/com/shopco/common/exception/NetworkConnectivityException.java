package com.shopco.common.exception;

public class NetworkConnectivityException extends RuntimeException {
    public NetworkConnectivityException(String message) {
        super(message);
    }
}
