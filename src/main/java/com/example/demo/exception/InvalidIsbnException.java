package com.example.demo.exception;

public class InvalidIsbnException extends RuntimeException {
  public InvalidIsbnException(String isbn) {
    super("ISBN invalide ou manquant : '" + isbn + "'");
  }
}
