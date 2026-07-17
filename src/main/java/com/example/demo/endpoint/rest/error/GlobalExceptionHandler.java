package com.example.demo.endpoint.rest.error;

import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.ExternalBookApiException;
import com.example.demo.exception.InvalidIsbnException;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centralizes error handling so controllers stay thin and clients never receive raw stack traces or
 * exception messages leaking implementation details.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(InvalidIsbnException.class)
  public ResponseEntity<ApiError> handleInvalidIsbn(InvalidIsbnException e) {
    return build(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(BookNotFoundException.class)
  public ResponseEntity<ApiError> handleBookNotFound(BookNotFoundException e) {
    return build(HttpStatus.NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(ExternalBookApiException.class)
  public ResponseEntity<ApiError> handleExternalApiFailure(ExternalBookApiException e) {
    log.warn("External book API call failed", e);
    return build(HttpStatus.BAD_GATEWAY, "Le service externe de livres est indisponible.");
  }

  private ResponseEntity<ApiError> build(HttpStatus status, String message) {
    return ResponseEntity.status(status)
        .body(new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message));
  }
}
