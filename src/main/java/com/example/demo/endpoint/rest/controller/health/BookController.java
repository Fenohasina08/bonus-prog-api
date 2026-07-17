package com.example.demo.endpoint.rest.controller.health;

import com.example.demo.dto.BookResponseDTO;
import com.example.demo.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookController {
  private final BookService bookService;

  @GetMapping("/books")
  public ResponseEntity<BookResponseDTO> searchBook(@RequestParam("isbn") String isbn) {
    return ResponseEntity.ok(bookService.findByIsbn(isbn));
  }
}
