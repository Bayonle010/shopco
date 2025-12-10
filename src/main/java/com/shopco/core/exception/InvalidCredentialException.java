package com.shopco.core.exception;

public class InvalidCredentialException extends RuntimeException {
  public InvalidCredentialException(String message) {
    super(message);
  }
}
