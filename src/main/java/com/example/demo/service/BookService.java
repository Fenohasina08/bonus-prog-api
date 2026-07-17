package com.example.demo.service;

import com.example.demo.dto.BookResponseDTO;
import com.example.demo.entity.Book;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.InvalidIsbnException;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.model.BookItem;
import com.example.demo.repository.model.GoogleBookResponse;
import com.example.demo.repository.model.VolumeInfo;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Orchestrates a book lookup by ISBN:
 *
 * <ol>
 *   <li>validate the ISBN,
 *   <li>return it from the database if we already fetched it before (avoids hammering the external
 *       API and creating duplicate rows on every call),
 *   <li>otherwise fetch it from Google Books, persist it, and return it.
 * </ol>
 */
@Service
@RequiredArgsConstructor
public class BookService {
  private static final String ISBN_PATTERN = "^[0-9Xx-]{9,17}$";

  private final BookRepository repository;
  private final GoogleBooksService googleBooksService;

  public BookResponseDTO findByIsbn(String isbn) {
    validate(isbn);

    return repository.findByIsbn(isbn).map(BookService::toDto).orElseGet(() -> fetchAndSave(isbn));
  }

  private BookResponseDTO fetchAndSave(String isbn) {
    GoogleBookResponse response = googleBooksService.fetchByIsbn(isbn);
    BookItem item = firstItemOrThrow(response, isbn);

    Book saved = repository.save(toEntity(isbn, item.getVolumeInfo()));
    return toDto(saved);
  }

  private static BookItem firstItemOrThrow(GoogleBookResponse response, String isbn) {
    if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
      throw new BookNotFoundException(isbn);
    }
    return response.getItems().get(0);
  }

  private static Book toEntity(String isbn, VolumeInfo volumeInfo) {
    return Book.builder()
        .id(UUID.randomUUID())
        .isbn(isbn)
        .title(volumeInfo.getTitle())
        .author(joinAuthors(volumeInfo.getAuthors()))
        .description(volumeInfo.getDescription())
        .build();
  }

  private static String joinAuthors(List<String> authors) {
    return authors == null || authors.isEmpty() ? "Auteur inconnu" : String.join(", ", authors);
  }

  private static BookResponseDTO toDto(Book book) {
    return BookResponseDTO.builder()
        .title(book.getTitle())
        .author(book.getAuthor())
        .description(book.getDescription())
        .build();
  }

  private static void validate(String isbn) {
    if (!StringUtils.hasText(isbn) || !isbn.matches(ISBN_PATTERN)) {
      throw new InvalidIsbnException(isbn);
    }
  }
}
