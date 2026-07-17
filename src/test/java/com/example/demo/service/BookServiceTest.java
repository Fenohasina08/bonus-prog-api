package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.dto.BookResponseDTO;
import com.example.demo.entity.Book;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.exception.InvalidIsbnException;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.model.BookItem;
import com.example.demo.repository.model.GoogleBookResponse;
import com.example.demo.repository.model.VolumeInfo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @Mock private BookRepository repository;
  @Mock private GoogleBooksService googleBooksService;
  @InjectMocks private BookService bookService;

  @Test
  void rejects_invalid_isbn() {
    assertThatThrownBy(() -> bookService.findByIsbn("not-an-isbn!!"))
        .isInstanceOf(InvalidIsbnException.class);
  }

  @Test
  void rejects_blank_isbn() {
    assertThatThrownBy(() -> bookService.findByIsbn("  ")).isInstanceOf(InvalidIsbnException.class);
  }

  @Test
  void returns_book_from_database_without_calling_external_api_when_already_known() {
    String isbn = "9782070368228";
    Book existing =
        Book.builder()
            .id(UUID.randomUUID())
            .isbn(isbn)
            .title("Le Petit Prince")
            .author("Antoine de Saint-Exupéry")
            .description("Un classique")
            .build();
    when(repository.findByIsbn(isbn)).thenReturn(Optional.of(existing));

    BookResponseDTO result = bookService.findByIsbn(isbn);

    assertThat(result.getTitle()).isEqualTo("Le Petit Prince");
    verify(googleBooksService, never()).fetchByIsbn(any());
  }

  @Test
  void fetches_from_google_books_and_persists_when_not_in_database() {
    String isbn = "9782070368228";
    when(repository.findByIsbn(isbn)).thenReturn(Optional.empty());

    VolumeInfo volumeInfo = new VolumeInfo();
    volumeInfo.setTitle("Le Petit Prince");
    volumeInfo.setAuthors(List.of("Antoine de Saint-Exupéry"));
    volumeInfo.setDescription("Un classique");

    BookItem item = new BookItem();
    item.setVolumeInfo(volumeInfo);

    GoogleBookResponse response = new GoogleBookResponse();
    response.setItems(List.of(item));

    when(googleBooksService.fetchByIsbn(isbn)).thenReturn(response);
    when(repository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

    BookResponseDTO result = bookService.findByIsbn(isbn);

    assertThat(result.getTitle()).isEqualTo("Le Petit Prince");
    assertThat(result.getAuthor()).isEqualTo("Antoine de Saint-Exupéry");
    verify(repository).save(any(Book.class));
  }

  @Test
  void throws_not_found_when_external_api_returns_no_results() {
    String isbn = "9780000000002";
    when(repository.findByIsbn(isbn)).thenReturn(Optional.empty());

    GoogleBookResponse emptyResponse = new GoogleBookResponse();
    emptyResponse.setItems(List.of());
    when(googleBooksService.fetchByIsbn(isbn)).thenReturn(emptyResponse);

    assertThatThrownBy(() -> bookService.findByIsbn(isbn))
        .isInstanceOf(BookNotFoundException.class);
  }
}
