package com.example.demo.repository.model;

import java.util.List;
import lombok.Data;

@Data
public class GoogleBookResponse {
  private List<BookItem> items;
}
