package com.example.demo.exception;

public class ExternalBookApiException extends RuntimeException {
  public ExternalBookApiException(String message, Throwable cause) {
    super(message, cause);
  }
}
