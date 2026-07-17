package com.example.demo.service;

import com.example.demo.exception.ExternalBookApiException;
import com.example.demo.repository.model.GoogleBookResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Thin HTTP client for the Google Books API. Only responsibility: turn an ISBN into a {@link
 * GoogleBookResponse}, or fail loudly with a domain exception instead of leaking RestTemplate
 * internals to the rest of the app.
 */
@Component
public class GoogleBooksService {
  private final RestTemplate restTemplate;
  private final String apiUrl;

  public GoogleBooksService(
      RestTemplate restTemplate, @Value("${google.books.api.url}") String apiUrl) {
    this.restTemplate = restTemplate;
    this.apiUrl = apiUrl;
  }

  public GoogleBookResponse fetchByIsbn(String isbn) {
    String url = apiUrl + "?q=isbn:" + isbn;
    try {
      return restTemplate.getForObject(url, GoogleBookResponse.class);
    } catch (RestClientException e) {
      throw new ExternalBookApiException(
          "Échec de l'appel à l'API Google Books pour l'ISBN " + isbn, e);
    }
  }
}
